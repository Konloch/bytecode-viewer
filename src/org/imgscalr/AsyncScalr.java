/**   
 * Copyright 2011 The Buzz Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.imgscalr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImagingOpException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.imgscalr.Scalr.Rotation;

/**
 * Class used to provide the asynchronous versions of all the methods defined in
 * {@link Scalr} for the purpose of efficiently handling large amounts of image
 * operations via a select number of processing threads asynchronously.
 * <p/>
 * Given that image-scaling operations, especially when working with large
 * images, can be very hardware-intensive (both CPU and memory), in large-scale
 * deployments (e.g. a busy web application) it becomes increasingly important
 * that the scale operations performed by imgscalr be manageable so as not to
 * fire off too many simultaneous operations that the JVM's heap explodes and
 * runs out of memory or pegs the CPU on the host machine, staving all other
 * running processes.
 * <p/>
 * Up until now it was left to the caller to implement their own serialization
 * or limiting logic to handle these use-cases. Given imgscalr's popularity in
 * web applications it was determined that this requirement be common enough
 * that it should be integrated directly into the imgscalr library for everyone
 * to benefit from.
 * <p/>
 * Every method in this class wraps the matching methods in the {@link Scalr}
 * class in new {@link Callable} instances that are submitted to an internal
 * {@link ExecutorService} for execution at a later date. A {@link Future} is
 * returned to the caller representing the task that is either currently
 * performing the scale operation or will at a future date depending on where it
 * is in the {@link ExecutorService}'s queue. {@link Future#get()} or
 * {@link Future#get(long, TimeUnit)} can be used to block on the
 * <code>Future</code>, waiting for the scale operation to complete and return
 * the resultant {@link BufferedImage} to the caller.
 * <p/>
 * This design provides the following features:
 * <ul>
 * <li>Non-blocking, asynchronous scale operations that can continue execution
 * while waiting on the scaled result.</li>
 * <li>Serialize all scale requests down into a maximum number of
 * <em>simultaneous</em> scale operations with no additional/complex logic. The
 * number of simultaneous scale operations is caller-configurable (see
 * {@link #THREAD_COUNT}) so as best to optimize the host system (e.g. 1 scale
 * thread per core).</li>
 * <li>No need to worry about overloading the host system with too many scale
 * operations, they will simply queue up in this class and execute in-order.</li>
 * <li>Synchronous/blocking behavior can still be achieved (if desired) by
 * calling <code>get()</code> or <code>get(long, TimeUnit)</code> immediately on
 * the returned {@link Future} from any of the methods below.</li>
 * </ul>
 * <h3>Performance</h3>
 * When tuning this class for optimal performance, benchmarking your particular
 * hardware is the best approach. For some rough guidelines though, there are
 * two resources you want to watch closely:
 * <ol>
 * <li>JVM Heap Memory (Assume physical machine memory is always sufficiently
 * large)</li>
 * <li># of CPU Cores</li>
 * </ol>
 * You never want to allocate more scaling threads than you have CPU cores and
 * on a sufficiently busy host where some of the cores may be busy running a
 * database or a web server, you will want to allocate even less scaling
 * threads.
 * <p/>
 * So as a maximum you would never want more scaling threads than CPU cores in
 * any situation and less so on a busy server.
 * <p/>
 * If you allocate more threads than you have available CPU cores, your scaling
 * operations will slow down as the CPU will spend a considerable amount of time
 * context-switching between threads on the same core trying to finish all the
 * tasks in parallel. You might still be tempted to do this because of the I/O
 * delay some threads will encounter reading images off disk, but when you do
 * your own benchmarking you'll likely find (as I did) that the actual disk I/O
 * necessary to pull the image data off disk is a much smaller portion of the
 * execution time than the actual scaling operations.
 * <p/>
 * If you are executing on a storage medium that is unexpectedly slow and I/O is
 * a considerable portion of the scaling operation (e.g. S3 or EBS volumes),
 * feel free to try using more threads than CPU cores to see if that helps; but
 * in most normal cases, it will only slow down all other parallel scaling
 * operations.
 * <p/>
 * As for memory, every time an image is scaled it is decoded into a
 * {@link BufferedImage} and stored in the JVM Heap space (decoded image
 * instances are always larger than the source images on-disk). For larger
 * images, that can use up quite a bit of memory. You will need to benchmark
 * your particular use-cases on your hardware to get an idea of where the sweet
 * spot is for this; if you are operating within tight memory bounds, you may
 * want to limit simultaneous scaling operations to 1 or 2 regardless of the
 * number of cores just to avoid having too many {@link BufferedImage} instances
 * in JVM Heap space at the same time.
 * <p/>
 * These are rough metrics and behaviors to give you an idea of how best to tune
 * this class for your deployment, but nothing can replacement writing a small
 * Java class that scales a handful of images in a number of different ways and
 * testing that directly on your deployment hardware.
 * <h3>Resource Overhead</h3>
 * The {@link ExecutorService} utilized by this class won't be initialized until
 * one of the operation methods are called, at which point the
 * <code>service</code> will be instantiated for the first time and operation
 * queued up.
 * <p/>
 * More specifically, if you have no need for asynchronous image processing
 * offered by this class, you don't need to worry about wasted resources or
 * hanging/idle threads as they will never be created if you never use this
 * class.
 * <h3>Cleaning up Service Threads</h3>
 * By default the {@link Thread}s created by the internal
 * {@link ThreadPoolExecutor} do not run in <code>daemon</code> mode; which
 * means they will block the host VM from exiting until they are explicitly shut
 * down in a client application; in a server application the container will shut
 * down the pool forcibly.
 * <p/>
 * If you have used the {@link AsyncScalr} class and are trying to shut down a
 * client application, you will need to call {@link #getService()} then
 * {@link ExecutorService#shutdown()} or {@link ExecutorService#shutdownNow()}
 * to have the threads terminated; you may also want to look at the
 * {@link ExecutorService#awaitTermination(long, TimeUnit)} method if you'd like
 * to more closely monitor the shutting down process (and finalization of
 * pending scale operations).
 * <h3>Reusing Shutdown AsyncScalr</h3>
 * If you have previously called <code>shutdown</code> on the underlying service
 * utilized by this class, subsequent calls to any of the operations this class
 * provides will invoke the internal {@link #checkService()} method which will
 * replace the terminated underlying {@link ExecutorService} with a new one via
 * the {@link #createService()} method.
 * <h3>Custom Implementations</h3>
 * If a subclass wants to customize the {@link ExecutorService} or
 * {@link ThreadFactory} used under the covers, this can be done by overriding
 * the {@link #createService()} method which is invoked by this class anytime a
 * new {@link ExecutorService} is needed.
 * <p/>
 * By default the {@link #createService()} method delegates to the
 * {@link #createService(ThreadFactory)} method with a new instance of
 * {@link DefaultThreadFactory}. Either of these methods can be overridden and
 * customized easily if desired.
 * <p/>
 * <strong>TIP</strong>: A common customization to this class is to make the
 * {@link Thread}s generated by the underlying factory more server-friendly, in
 * which case the caller would want to use an instance of the
 * {@link ServerThreadFactory} when creating the new {@link ExecutorService}.
 * <p/>
 * This can be done in one line by overriding {@link #createService()} and
 * returning the result of:
 * <code>return createService(new ServerThreadFactory());</code>
 * <p/>
 * By default this class uses an {@link ThreadPoolExecutor} internally to handle
 * execution of queued image operations. If a different type of
 * {@link ExecutorService} is desired, again, simply overriding the
 * {@link #createService()} method of choice is the right way to do that.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 * @since 3.2
 */
@SuppressWarnings("javadoc")
public class AsyncScalr {
	/**
	 * System property name used to set the number of threads the default
	 * underlying {@link ExecutorService} will use to process async image
	 * operations.
	 * <p/>
	 * Value is "<code>imgscalr.async.threadCount</code>".
	 */
	public static final String THREAD_COUNT_PROPERTY_NAME = "imgscalr.async.threadCount";

	/**
	 * Number of threads the internal {@link ExecutorService} will use to
	 * simultaneously execute scale requests.
	 * <p/>
	 * This value can be changed by setting the
	 * <code>imgscalr.async.threadCount</code> system property (see
	 * {@link #THREAD_COUNT_PROPERTY_NAME}) to a valid integer value &gt; 0.
	 * <p/>
	 * Default value is <code>2</code>.
	 */
	public static final int THREAD_COUNT = Integer.getInteger(
			THREAD_COUNT_PROPERTY_NAME, 2);

	/**
	 * Initializer used to verify the THREAD_COUNT system property.
	 */
	static {
		if (THREAD_COUNT < 1)
			throw new RuntimeException("System property '"
					+ THREAD_COUNT_PROPERTY_NAME + "' set THREAD_COUNT to "
					+ THREAD_COUNT + ", but THREAD_COUNT must be > 0.");
	}

	protected static ExecutorService service;

	/**
	 * Used to get access to the internal {@link ExecutorService} used by this
	 * class to process scale operations.
	 * <p/>
	 * <strong>NOTE</strong>: You will need to explicitly shutdown any service
	 * currently set on this class before the host JVM exits.
	 * <p/>
	 * You can call {@link ExecutorService#shutdown()} to wait for all scaling
	 * operations to complete first or call
	 * {@link ExecutorService#shutdownNow()} to kill any in-process operations
	 * and purge all pending operations before exiting.
	 * <p/>
	 * Additionally you can use
	 * {@link ExecutorService#awaitTermination(long, TimeUnit)} after issuing a
	 * shutdown command to try and wait until the service has finished all
	 * tasks.
	 * 
	 * @return the current {@link ExecutorService} used by this class to process
	 *         scale operations.
	 */
	public static ExecutorService getService() {
		return service;
	}

	/**
	 * @see Scalr#apply(BufferedImage, BufferedImageOp...)
	 */
	public static Future<BufferedImage> apply(final BufferedImage src,
			final BufferedImageOp... ops) throws IllegalArgumentException,
			ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.apply(src, ops);
			}
		});
	}

	/**
	 * @see Scalr#crop(BufferedImage, int, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> crop(final BufferedImage src,
			final int width, final int height, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.crop(src, width, height, ops);
			}
		});
	}

	/**
	 * @see Scalr#crop(BufferedImage, int, int, int, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> crop(final BufferedImage src,
			final int x, final int y, final int width, final int height,
			final BufferedImageOp... ops) throws IllegalArgumentException,
			ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.crop(src, x, y, width, height, ops);
			}
		});
	}

	/**
	 * @see Scalr#pad(BufferedImage, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> pad(final BufferedImage src,
			final int padding, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.pad(src, padding, ops);
			}
		});
	}

	/**
	 * @see Scalr#pad(BufferedImage, int, Color, BufferedImageOp...)
	 */
	public static Future<BufferedImage> pad(final BufferedImage src,
			final int padding, final Color color, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.pad(src, padding, color, ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final int targetSize, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, targetSize, ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, Method, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final Method scalingMethod, final int targetSize,
			final BufferedImageOp... ops) throws IllegalArgumentException,
			ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, scalingMethod, targetSize, ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, Mode, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final Mode resizeMode, final int targetSize,
			final BufferedImageOp... ops) throws IllegalArgumentException,
			ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, resizeMode, targetSize, ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, Method, Mode, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final Method scalingMethod, final Mode resizeMode,
			final int targetSize, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, scalingMethod, resizeMode, targetSize,
						ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, int, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final int targetWidth, final int targetHeight,
			final BufferedImageOp... ops) throws IllegalArgumentException,
			ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, targetWidth, targetHeight, ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, Method, int, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final Method scalingMethod, final int targetWidth,
			final int targetHeight, final BufferedImageOp... ops) {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, scalingMethod, targetWidth,
						targetHeight, ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, Mode, int, int, BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final Mode resizeMode, final int targetWidth,
			final int targetHeight, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, resizeMode, targetWidth, targetHeight,
						ops);
			}
		});
	}

	/**
	 * @see Scalr#resize(BufferedImage, Method, Mode, int, int,
	 *      BufferedImageOp...)
	 */
	public static Future<BufferedImage> resize(final BufferedImage src,
			final Method scalingMethod, final Mode resizeMode,
			final int targetWidth, final int targetHeight,
			final BufferedImageOp... ops) throws IllegalArgumentException,
			ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.resize(src, scalingMethod, resizeMode,
						targetWidth, targetHeight, ops);
			}
		});
	}

	/**
	 * @see Scalr#rotate(BufferedImage, Rotation, BufferedImageOp...)
	 */
	public static Future<BufferedImage> rotate(final BufferedImage src,
			final Rotation rotation, final BufferedImageOp... ops)
			throws IllegalArgumentException, ImagingOpException {
		checkService();

		return service.submit(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				return Scalr.rotate(src, rotation, ops);
			}
		});
	}

	protected static ExecutorService createService() {
		return createService(new DefaultThreadFactory());
	}

	protected static ExecutorService createService(ThreadFactory factory)
			throws IllegalArgumentException {
		if (factory == null)
			throw new IllegalArgumentException("factory cannot be null");

		return Executors.newFixedThreadPool(THREAD_COUNT, factory);
	}

	/**
	 * Used to verify that the underlying <code>service</code> points at an
	 * active {@link ExecutorService} instance that can be used by this class.
	 * <p/>
	 * If <code>service</code> is <code>null</code>, has been shutdown or
	 * terminated then this method will replace it with a new
	 * {@link ExecutorService} by calling the {@link #createService()} method
	 * and assigning the returned value to <code>service</code>.
	 * <p/>
	 * Any subclass that wants to customize the {@link ExecutorService} or
	 * {@link ThreadFactory} used internally by this class should override the
	 * {@link #createService()}.
	 */
	protected static void checkService() {
		if (service == null || service.isShutdown() || service.isTerminated()) {
			/*
			 * If service was shutdown or terminated, assigning a new value will
			 * free the reference to the instance, allowing it to be GC'ed when
			 * it is done shutting down (assuming it hadn't already).
			 */
			service = createService();
		}
	}

	/**
	 * Default {@link ThreadFactory} used by the internal
	 * {@link ExecutorService} to creates execution {@link Thread}s for image
	 * scaling.
	 * <p/>
	 * More or less a copy of the hidden class backing the
	 * {@link Executors#defaultThreadFactory()} method, but exposed here to make
	 * it easier for implementors to extend and customize.
	 * 
	 * @author Doug Lea
	 * @author Riyad Kalla (software@thebuzzmedia.com)
	 * @since 4.0
	 */
	protected static class DefaultThreadFactory implements ThreadFactory {
		protected static final AtomicInteger poolNumber = new AtomicInteger(1);

		protected final ThreadGroup group;
		protected final AtomicInteger threadNumber = new AtomicInteger(1);
		protected final String namePrefix;

		DefaultThreadFactory() {
			SecurityManager manager = System.getSecurityManager();

			/*
			 * Determine the group that threads created by this factory will be
			 * in.
			 */
			group = (manager == null ? Thread.currentThread().getThreadGroup()
					: manager.getThreadGroup());

			/*
			 * Define a common name prefix for the threads created by this
			 * factory.
			 */
			namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		/**
		 * Used to create a {@link Thread} capable of executing the given
		 * {@link Runnable}.
		 * <p/>
		 * Thread created by this factory are utilized by the parent
		 * {@link ExecutorService} when processing queued up scale operations.
		 */
		public Thread newThread(Runnable r) {
			/*
			 * Create a new thread in our specified group with a meaningful
			 * thread name so it is easy to identify.
			 */
			Thread thread = new Thread(group, r, namePrefix
					+ threadNumber.getAndIncrement(), 0);

			// Configure thread according to class or subclass
			thread.setDaemon(false);
			thread.setPriority(Thread.NORM_PRIORITY);

			return thread;
		}
	}

	/**
	 * An extension of the {@link DefaultThreadFactory} class that makes two
	 * changes to the execution {@link Thread}s it generations:
	 * <ol>
	 * <li>Threads are set to be daemon threads instead of user threads.</li>
	 * <li>Threads execute with a priority of {@link Thread#MIN_PRIORITY} to
	 * make them more compatible with server environment deployments.</li>
	 * </ol>
	 * This class is provided as a convenience for subclasses to use if they
	 * want this (common) customization to the {@link Thread}s used internally
	 * by {@link AsyncScalr} to process images, but don't want to have to write
	 * the implementation.
	 * 
	 * @author Riyad Kalla (software@thebuzzmedia.com)
	 * @since 4.0
	 */
	protected static class ServerThreadFactory extends DefaultThreadFactory {
		/**
		 * Overridden to set <code>daemon</code> property to <code>true</code>
		 * and decrease the priority of the new thread to
		 * {@link Thread#MIN_PRIORITY} before returning it.
		 */
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = super.newThread(r);

			thread.setDaemon(true);
			thread.setPriority(Thread.MIN_PRIORITY);

			return thread;
		}
	}
}