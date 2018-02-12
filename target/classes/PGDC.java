
public class PGDC{

	public static boolean estPremier(int n){
		int i=2;
		while(i<n && n%i !=0)
			i++;
		return (i==n);
	}
	public static int premierSuivant(int n){
		
		n++;
		while( !estPremier(n))
			n++;
		return n;
	}
	public static int[] decomposerFP(int n){
		int d=2, i=0;
		int[] tab = new int[n];
		while(n>1){
			if(n%d == 0){
				tab[i]=d;
				tab[i+1] +=1;
				n = n/d;
			}else{
				d = premierSuivant(d);
				i+=2;
			}
			
		}
		
	}
	public static boolean existe(int x, int[] tab){
		int i=0;
		while(i<tab.length && tab[i]!=x)
			i++;
		return (i<tab.length);
	}
	public static void main(String[] args){
 
		int a=Integer.parseInt(args[0]), b=Integer.parseInt(args[1]), pgcd=1;
	
		int[] t1 = decomposerFP(a);
		int[] t2 = decomposerFP(b);
		
		int x,p;
		for(int i=0; i<t1.length; i+=2){
			x=t1[i];
			if(existe(x, t2)){
				p = (t1[i+1] < t2[i+1]) ? t1[i+1] : t2[i+1];
				pgcd *=  Math.pow(x, p);
			}
		}
		System.out.println(pgcd);
	}
	
