package the.bytecode.club.uikit.tabpopup.closer;

/**
 * PopupMenu items configuration of close tabs
 * @author su
 *
 */
public class PopupMenuTabsCloseConfiguration {
	private boolean close;
	private boolean closeOthers;
	private boolean closeAll;
	private boolean closeLefts;
	private boolean closeRights;
	
	public PopupMenuTabsCloseConfiguration(Builder builder) {
		super();
		this.close = builder.close;
		this.closeOthers = builder.closeOthers;
		this.closeAll = builder.closeAll;
		this.closeLefts = builder.closeLefts;
		this.closeRights = builder.closeRights;
	}
	public boolean isClose() {
		return close;
	}
	public void Close(boolean close) {
		this.close = close;
	}
	public boolean isCloseOthers() {
		return closeOthers;
	}
	public void setCloseOthers(boolean closeOthers) {
		this.closeOthers = closeOthers;
	}
	public boolean isCloseAll() {
		return closeAll;
	}
	public void setCloseAll(boolean closeAll) {
		this.closeAll = closeAll;
	}
	public boolean isCloseLefts() {
		return closeLefts;
	}
	public void setCloseLefts(boolean closeLefts) {
		this.closeLefts = closeLefts;
	}
	public boolean isCloseRights() {
		return closeRights;
	}
	public void setCloseRights(boolean closeRights) {
		this.closeRights = closeRights;
	}
	
	public static class Builder {
		private boolean close;
		private boolean closeOthers;
		private boolean closeAll;
		private boolean closeLefts;
		private boolean closeRights;
		
		public Builder close(boolean close) {
			this.close = close;
			return this;
		}
		
		public Builder closeOthers(boolean closeOthers) {
			this.closeOthers = closeOthers;
			return this;
		}
		
		public Builder closeAll(boolean closeAll) {
			this.closeAll = closeAll;
			return this;
		}
		
		public Builder closeLefts(boolean closeLefts) {
			this.closeLefts = closeLefts;
			return this;
		}
		
		public Builder closeRights(boolean closeRights) {
			this.closeRights = closeRights;
			return this;
		}
		
		public PopupMenuTabsCloseConfiguration build() {
			return new PopupMenuTabsCloseConfiguration(this);
		}
		
		public PopupMenuTabsCloseConfiguration buildFull() {
			return this.close(true).
			closeOthers(true).
			closeAll(true).
			closeLefts(true).
			closeRights(true).
			build();
		}
	}
}
