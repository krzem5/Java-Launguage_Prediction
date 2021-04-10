package com.krzem.launguage_prediction;
import com.krzem.NN.NeuralNetwork;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.Paths;



class LangPrediction{
	public static final String[] LANGS={"pl","en"};
	public static final String ALPHABET="abcdefghijklmnopqrstuvwxyz";
	public static final int MAX_LETTERS=12;
	public static final int DATASET_SIZE=1000;
	public static String[][] WORDS;
	public NeuralNetwork NN;




	public LangPrediction(){
		this.NN=NeuralNetwork.fromFile("./data");
		this._load_words();
	}



	public double[] encode_word(String w){
		w=w.toLowerCase();
		double[] e=new double[MAX_LETTERS*ALPHABET.length()];
		int i=0;
		for (int j=0;j<w.length();j++){
			if (j>=MAX_LETTERS||ALPHABET.indexOf(w.charAt(j))==-1){
				break;
			}
			e[i*ALPHABET.length()+ALPHABET.indexOf(w.charAt(j))]=1;
			i++;
		}
		return e;
	}



	public void predict(String W){
		double[] o=this.NN.predict(this.encode_word(W));
		double s=0;
		for (double v:o){
			s+=v;
		}
		int i=0;
		for (double v:o){
			o[i]=v/s;
			i++;
		}
		i=0;
		for (String k:this.LANGS){
			System.out.println(String.format("%s: %s -> %f",W,k,o[i]));
			i++;
		}
	}



	public void predict_sentence(String S){
		double[] o=new double[this.LANGS.length];
		int j=0;
		for (String W:S.split(" ")){
			double[] p=this.NN.predict(this.encode_word(W));
			double s=0;
			for (double v:p){
				s+=v;
			}
			int i=0;
			for (double v:p){
				o[i]+=v/s;
				i++;
			}
			j++;
		}
		int i=0;
		for (double v:o){
			o[i]=v/j;
			i++;
		}
		i=0;
		for (String k:this.LANGS){
			System.out.println(String.format("%s: %s -> %f",S,k,o[i]));
			i++;
		}
	}



	public void test(){
		this.NN.test(this._batch());
	}



	public void train(int i){
		this.NN.train_multiple(this._batch(),i,0,10000,true);
	}



	public double acc(){
		return this.NN.acc(this._batch());
	}



	private void _load_words(){
		try{
			this.WORDS=new String[this.LANGS.length][];
			int i=0;
			for (String l:this.LANGS){
				this.WORDS[i]=new String(Files.readAllBytes(Paths.get(String.format("./data/%s.txt",l))),"UTF-8").split("\n");
				i++;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private double[] encode_lang(int i){
		double[] o=new double[this.LANGS.length];
		o[i]=1;
		return o;
	}



	private double[][][] _batch(){
		double[][][] d=new double[this.DATASET_SIZE*this.LANGS.length][][];
		int i=0;
		int j=0;
		for (String l:this.LANGS){
			for (String w:this.WORDS[i]){
				double[][] dt={this.encode_word(w),this.encode_lang(i)};
				d[j]=dt;
				j++;
			}
			i++;
		}
		return d;
	}
}



public class Main{
	public static void main(String[] args){
		LangPrediction l=new LangPrediction();
		if (true){
			System.out.println(l.acc());
			l.predict_sentence("Aj holp god.");
			l.predict_sentence("Hi, how are you?");
			l.predict_sentence("Hej");
		}
		else{
			System.out.println(l.acc());
			l.train(5000);
			l.NN.toFile("./data");
			System.out.println(l.acc());
		}
	}
}
