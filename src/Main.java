import java.util.ArrayList;
import java.util.HashMap;

import chart.Chart;
import chart.ChartFrame;
import model.Adder;
import model.AtomicComponent;
import model.Euler;
import model.Qss;
import model.Step1;
import model.Step2;
import model.Step3;
import model.Step4;



public class Main {

	public static void main(String args[]){
		
		//Attention
		//Les ports sont connectÃ©s par leur nom: pour connecter deux ports, ils doivent avoir le mÃªme nom
		//Pour l'affichage des charts, deux composants ne peuvent pas avoir le mÃªme nom de variable
		
		
		ArrayList<AtomicComponent> components = new ArrayList<>();
		
		//******Instantiation des composants atomiques ****** INSERER ICI VOS COMPOSANTS
		
		Step1 step1 = new Step1("step1");
		Step2 step2 = new Step2("step2");
		Step3 step3 = new Step3("step3");
		Step4 step4 = new Step4("step4");
		Adder adder = new Adder("adder");
		Euler euler = new Euler("euler");
		Qss   qss   = new Qss("qss");
		
		components.add(step1);
		components.add(step2);
		components.add(step3);
		components.add(step4);
		components.add(adder);
		components.add(euler);
		components.add(qss);

		// CrÃ©ation du Frame Chart   METTRE UN TITRE
		ChartFrame chart = new ChartFrame("","test de l'Integrateur");	

		//**************************************************

		
		//******Initialisation des composants atomiques ****** NE PAS MODIFIER
		
		for(int i = 0; i < components.size() ; i++){
			components.get(i).init();
			components.get(i).setTr(components.get(i).getTa());
		}

		//**************************************************
			

		//******Gestion de l'affichage des rÃ©sultats*******   NE PAS MODIFIER
		
		// Cette HashMap associe un atomique Ã  chaque variable (utile pour l'envoi de la valeur pendant la simu)
		HashMap<String, AtomicComponent> vars_atom = new HashMap<>();
		ArrayList<String> integer_variables = new ArrayList<>();
		ArrayList<String> real_variables = new ArrayList<>();
		for(AtomicComponent a : components){
			for(String s : a.getIntegerVariables()){
				integer_variables.add(s);
				vars_atom.put(s, a);
			}
			
			for(String s : a.getRealVariables()){
				real_variables.add(s);
				vars_atom.put(s, a);
			}
		}

		// Cette HashMap associe une trajectoire (Chart) Ã  chaque variable
		HashMap<String, Chart> vars_chart = new HashMap<>();
		for(String var : integer_variables){
			Chart c = new Chart(var);
			chart.addToLineChartPane(c);
			vars_chart.put(var, c);
		}
		
		for(String var : real_variables){
			Chart c = new Chart(var);
			chart.addToLineChartPane(c);
			vars_chart.put(var, c);
		}
		
		//**************************************************
		
		double t = 0;
		double tn = 0;
		double tfin = 2;
		//BOUCLE DE SIMULATION
		while(t < tfin){

			System.out.println("****************");
			System.out.println("t=" + t);
			ArrayList<AtomicComponent> imminentComponent = new ArrayList<AtomicComponent>();
			 HashMap<String, Integer> outputs = new  HashMap<String, Integer>();

			double tmin = Double.POSITIVE_INFINITY;		// tmin a l'infini
			/*  Parcours de Tous Les eléments*/
			for(AtomicComponent elem : components) 
			{	// Pour tous les éléments dela simulation
				
				if(elem.getTr()<tmin)// Si le Ta de l'élément est inférieur a tmin, 
				{
						tmin = elem.getTr()	;							// Alors je met a jour tmin, et je regarde 
						imminentComponent.clear();												// si des composants imminents ont le meme tmin
						imminentComponent.add(elem)	;																	// Alors j'ajoute l'élément aux composant iminents
						
							
				}
				else if(elem.getTa()==tmin) 
				{
					imminentComponent.add(elem);
				}
				
			}
// import 
			/* Exécution sortie des éléments imminents  */
			for(AtomicComponent i : imminentComponent) 
			{
				System.out.println("\t"+i.getName() + ": lambda(S) : "+ i.lambda());
				for(String i_lambda : i.lambda() )
				{
				outputs.put( i_lambda,
							 i.readIntegerOutputValue( i_lambda ) 
							) ;
				}
			}	
			
			/* Boucle pour delta --> TRANSITIONS */
			for(AtomicComponent b : components) 		
			{
				String text = "début:"+b;
				
				ArrayList<String>  c = new  ArrayList<String>(b.getInputs()); // copies des entrées de b dans c
				c.retainAll( outputs.keySet() ); // garde dans c que les elements présent dans outputs
				
				boolean asEvolute = false;
				b.setE(tmin);
				if(imminentComponent.contains(b))/*imminent*/
				{
					
					if( c.isEmpty()) /*si vide : pas d'entrées*/  /*pas d'entrée*/
					{
						b.delta_int();
						asEvolute=true;
					}
					else /* entrée*/
					{
						b.delta_con(outputs.keySet());
						asEvolute=true;
					}
				
					
					// mise a jour du temps si imminent
				}
				else
					{
						if(!c.isEmpty() )/* pas imminent && entrée*/ 
						{
							b.delta_ext(outputs);
							asEvolute=true;
						}
						// mise a jour du tr de tout les élements
					}
				b.setTl(tmin);
				 if (asEvolute)
				 {
					 b.setTr(b.getTa());						 
				 }
				 else
				 {
						b.setTr(b.getTr()-tmin); 
				 }
			}
			
			t = t+tmin;
 // fin import
			
			
			//Envoie des donnÃ©es aux charts ******NE PAS MODIFIER********
			for(String var : integer_variables)
				vars_chart.get(var).addDataToSeries(t, vars_atom.get(var).getIntegerValue(var));
			
			for(String var : real_variables)
				vars_chart.get(var).addDataToSeries(t, vars_atom.get(var).getRealValue(var));

			
		}
	}
	
	
}
