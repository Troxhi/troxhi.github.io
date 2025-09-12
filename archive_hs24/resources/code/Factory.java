public class Factory {
	/*
	 * Zur Erinnerung: Sie duerfern die Anweisungen der Methode computeCost aendern
	 * aber nicht die Parameterliste oder den Rueckgabewert Wenn es keine public
	 * static Methode mit diesem Namen und Rueckgabewert gibt, erhalten Sie 0 Punkte
	 * Sie duerfen in diese Klasse weitere Methoden hinzufuegen
	 */
	public static Cost computeCost(Part[] steps) {
		Cost c = new Cost();
		boolean addLuxuryTax = false;

		if (multipleFluxkompensator(steps) || fluxkompensatorBeforeSchwebeumwandlungOrOutatimeKennzeichen(steps)) {
			return null;
		}
		
		for (Part p : steps) {
			if (p instanceof FirstEditionFluxkompensator || p instanceof VerchromteRaeder) {
				addLuxuryTax = true;
			}
			p.process(c);
		}
		
		if (addLuxuryTax) {
			c.luxuryTax = c.productionCost*5/100;
		}

		return c;
	}
	
	private static boolean fluxkompensatorBeforeSchwebeumwandlungOrOutatimeKennzeichen(Part[] parts) {
		boolean fluxkompensatorOccurred = false;
		for (Part part : parts) {
			if (fluxkompensatorOccurred && (part instanceof Schwebeumwandlung || part instanceof OutatimeKennzeichen)) {
				return true;
			}
			if (isFluxkompensator(part)) {
				fluxkompensatorOccurred = true;
			}
		}
		return false;
	}
	
	private static boolean multipleFluxkompensator(Part[] parts) {
		int count = 0;
		for (Part part : parts) {
			if (isFluxkompensator(part)) {
				count++;
			}
		}
		return count > 1;
	}
	
	private static boolean isFluxkompensator(Part part) {
		return part instanceof Fluxkompensator && !(part instanceof FirstEditionFluxkompensator);
	}

	public static void main(String[] args) {
		Cost c = Factory.computeCost(new Part[] { 
				new EinklappbareRaeder(), 
				new Fluxkompensator() });
		
		System.out.println("Die Kosten betragen sich auf " + c.productionCost + " (MWst " + c.vat + ", Luxus-Steuer "
				+ c.luxuryTax + ")");
	}
}

class Part {
	
	public void process(Cost c) {
		// do nothing
	}
}

class Fluegeltueren extends Part {
	
	@Override
	public void process(Cost c) {
		c.productionCost += 2000;
		c.vat += 2000*3/100;
	}
}

class Fluxkompensator extends Part {
	
	@Override
	public void process(Cost c) {
		int oldProductionCost = c.productionCost;
		int newProductionCost = 2*oldProductionCost;
		
		c.productionCost = newProductionCost;
		
		int diff = newProductionCost - oldProductionCost;
		c.vat += diff*7/100;		
	}
}

class Schwebeumwandlung extends Part {
	
	@Override
	public void process(Cost c) {
		int oldProductionCost = c.productionCost;
		int newProductionCost = oldProductionCost*120/100;
		
		c.productionCost = newProductionCost;
		
		int diff = newProductionCost - oldProductionCost;
		c.vat += diff*10/100;
		
	}
}

class EinklappbareRaeder extends Part {
	
	@Override
	public void process(Cost c) {
		int oldProductionCost = c.productionCost;
		int newProductionCost = Math.min(oldProductionCost+7000, Math.max(100_000, oldProductionCost));
		
		c.productionCost = newProductionCost;
		
		int diff = newProductionCost - oldProductionCost;
		c.vat += diff*7/100;
	}
}

class OutatimeKennzeichen extends Part {
	
	@Override
	public void process(Cost c) {
		int oldProductionCost = c.productionCost;
		int newProductionCost = Math.min(oldProductionCost+100, Math.max(50_000, oldProductionCost));
		
		c.productionCost = newProductionCost;
		
		int diff = newProductionCost - oldProductionCost;
		c.vat += diff*10/100;		
	}
}

//erst relevant in Teil (b)
class FirstEditionFluxkompensator extends Fluxkompensator {
	
}

//erst relevant in Teil (b)
class VerchromteRaeder extends EinklappbareRaeder {
	
}

class Cost {
	public int productionCost = 0;
	public int vat = 0;
	public int luxuryTax = 0; 
}

