package model;
import java.util.ArrayList;

public class Qss extends AtomicComponent{
	
	private double sigma;  	// Temps d'attente sur l'état :		delta_t 
	private double d;		// Taille de l'échantillon d'état:	delta_q
	private double dl;		// Numérateur de delta_t :			d - Math.signum(x-xl) <==> delta_q - abs(q _ q_init)
	private double dx;		// Dérivé de q 						dq
	private double x;		// q								q
	private double xl;		// q initial						q_l
	
	public Qss(String name){
		super(name);
		outputs.add("qss_s0");
		inputs.add("adder_s0");
	}
	
	public void init() {
		sigma	= 0;
		d		= 0.1;
		dl		= 0;
		dx		= 0;
		x		= 0;
		xl		= 0;
		real_varnames_var.put("qx", x);
		current_state = 0;
	}

	public void delta_int(){  // le cas d'un échantillon a dérivée constante
		double sigmadebut = sigma;
		if(current_state == 0){
			
			// Partie modifiée : début
			x		= xl + Math.signum(dx)*d;   // q 					= q_l + sign(dq)*delta_q
			xl		= x ;						// q_l 					= q 
			dl		= d ;						// numerateur(delta_t) 	= delta_q
			sigma	= dl/Math.abs(dx) ;			// delta_t				= numerateur(delta_t) / abs(dq)
			// Partie modifiée : fin			
			real_varnames_var.put("qx", x);
			next_state = 0;
		}
		current_state = next_state;
		System.out.println("delta_int ("+sigmadebut+" => " + sigma + ")");
	}

	public void delta_ext(ArrayList<String> inputs){ // le cas d'un échantillon a dérivée qui varie
		double sigmadebut = sigma;
		if(current_state == 0 && inputs.contains("adder_s0")){
			dx = real_inputnames_input.get("adder_s0");
			// Partie modifiée : début
			x		= xl + Math.signum(dx)*e ;						// q 					= q_l + sign(dq)*e
			dl		= d - Math.abs(x-xl) ;		// numerateur(delta_t) 	= delta_q - abs(q q_l)
			sigma	= dl/Math.abs(dx) ;			// delta_t				= numerateur(delta_t) / abs(dq)
			// Partie modifiée : fin
			real_varnames_var.put("qx", x);
			next_state = 0;
		}
		current_state = next_state;
		System.out.println("delta_ext ("+sigmadebut+" => " + sigma + ")");
	}
	
	public void delta_con(ArrayList<String> inputs){
		double sigmadebut = sigma;
		if(current_state == 0 && inputs.contains("adder_s0")){
			dx = real_inputnames_input.get("adder_s0");
			
			//x		= 0 ;// TODO a changer
			//dl		= 0 ;// TODO a changer
			//sigma	= 0 ;// TODO a changer
			
			real_varnames_var.put("qx", x);
			next_state = 0;
		}
		
		current_state = next_state;
		System.out.println("delta_con ("+sigmadebut+" => " + sigma + ")");
	}

	public ArrayList<String> lambda(){
		ArrayList<String> outputs = new ArrayList<>();
		if(current_state == 0){
			outputs.add("qss_s0");
			real_outputnames_output.put("qss_s0", 0.0/*TODO a changer*/);
		}
		return outputs;
	}

	public double getTa(){

		if(current_state == 0){
			return sigma;
		}
		return 0;
	}
	
	
}