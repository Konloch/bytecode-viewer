// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class TryWithResources extends RuntimeException implements AutoCloseable {
    public static TryWithResources $ = new TryWithResources(5);
    public final int i;

    static public TryWithResources $(int i) {return new TryWithResources(i);}
    public TryWithResources(int i) {System.out.println("Opened "+i); this.i=new int[i].length;}
    public void close() {System.out.println("Closed "+i); if(i%4==1) {throw this;}}
    static public void $(int i, RuntimeException e) {if(null != e) {throw e;}}
    static public void $(RuntimeException e) {System.out.println(e);}
    public static void main(String[] args) throws Throwable {$.main();}

    public void main(String[]... args)
    {
        try{
        int i=-123,j=4567;

        try{++i;i++;} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        System.out.println(i); System.out.println(j);

        try(TryWithResources $=null) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(0)) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(-3)) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(0)) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1)) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1)) {$(++i, $);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1)) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        System.out.println(i); System.out.println(j);

        try(AutoCloseable _=null; TryWithResources $=null) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$(0)) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$(-3)) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$(0)) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$(1)) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$(1)) {$(++i, $);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$(1)) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(AutoCloseable _=null; TryWithResources $=this.$) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        System.out.println(i); System.out.println(j);

        try(TryWithResources $=this.$(0);AutoCloseable _=null;) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(-3);AutoCloseable _=null;) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(0);AutoCloseable _=null;) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1);AutoCloseable _=null;) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1);AutoCloseable _=null;) {$(++i, $);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1);AutoCloseable _=null;) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$;AutoCloseable _=null;) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        System.out.println(i); System.out.println(j);

        try(TryWithResources $=this.$(0); TryWithResources $$=$) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(-3); TryWithResources $$=$) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(0); TryWithResources $$=$) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1); TryWithResources $$=$) {$(++i, null);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1); TryWithResources $$=$) {$(++i, $);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$(1); TryWithResources $$=$) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        try(TryWithResources $=this.$; TryWithResources $$=$) {$(++i, this.$);} catch(RuntimeException _) {$(_);j++;} finally {int k=k=i;i=j;j=k;}
        System.out.println(i); System.out.println(j);
        } catch (Throwable t) {t.printStackTrace();}
    }
}