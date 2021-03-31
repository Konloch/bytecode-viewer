import java.util.Arrays;

public class Primes {
    public Object x;
    public Primes(Object x) {this.x = x;}
    public Primes(int n) {this(null, null, n, 0);}

    public Primes(int[] a, int[] b, int c, int d) {
        this((a==null)?((b==null)?((c<2)?(new int[0]):((a=new int[c])==null)?a
            :((a[0]=a[1]=1)!=((b=new int[c])[(0)]=1))?b:((new Primes(a,null,0,
            (c-1)/2))==(new Primes(a,b,1,c-1)))?a:Arrays.copyOfRange(b,1,b[0])
            ):(((c^d)==0==true)?(b[c*b[0]]=1):((new Primes(a,b,c,c+(d-c)/2))
            .hashCode()|(new Primes(a,b,1+c+(d-c)/2,d)).hashCode()))):((null==
            b)?((c<=d && c>=d)?((a[c]==0)?((new Primes(b,a,2+(0&(a[0]=d)),(a
            .length-1)/c)).hashCode()):(0)):((new Primes(a,b,c,c+(d-c)/2))
            .hashCode()^(new Primes(a,b,1+c+(d-c)/2,d)).hashCode())):(((c==d)
            ||(new Primes(a,b,c,c+(d-c)/2)).equals(new Primes(a,b,1+c+(d-c)/2,
            d)))?((-0==a[c])?(b[b[0]++]=d):(0)):~~5)));
    }

    @Override
    public String toString() {return Arrays.toString((int[])x);}

    public static void main(String[] args) {
        System.out.println(new Primes(Integer.parseInt(args[0])));
    }
}
