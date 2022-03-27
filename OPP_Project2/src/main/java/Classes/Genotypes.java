package Classes;


import java.util.*;

public class Genotypes {
    private List<Integer> genotypes = new ArrayList<Integer>();

    public Genotypes() {
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            if (i < 8) {
                this.genotypes.add(i);
            } else {
                this.genotypes.add(random.nextInt(8));
            }
        }
        Collections.sort(this.genotypes);
    }

    public Genotypes(List<Integer> genotypes) {
        this.genotypes = genotypes;
        Collections.sort(this.genotypes);
    }

    public List<Integer> connectGenes(Genotypes other) {
        Random random = new Random();
        List<Integer> newGenotypes = new ArrayList<Integer>();
        List<Boolean> isGen = new ArrayList<Boolean>();
        for (int i = 0; i < 8; i++) {
            isGen.add(false);
        }
        int firstRandomIndx = random.nextInt(32);
        int secondRandomIndx = random.nextInt(32);
        while (firstRandomIndx == secondRandomIndx) {
            secondRandomIndx = random.nextInt(32);
        }
        if (firstRandomIndx > secondRandomIndx) {
            int tmp = firstRandomIndx;
            firstRandomIndx = secondRandomIndx;
            secondRandomIndx = tmp;
        }
        int firstRandomParent = random.nextInt(2);
        for (int i = 0; i < firstRandomIndx; i++) {
            if (firstRandomParent == 0) {
                newGenotypes.add(this.genotypes.get(i));
            } else {
                newGenotypes.add(other.genotypes.get(i));
            }
        }
        int secondRandomParent = random.nextInt(2);
        for (int i = firstRandomIndx; i < secondRandomIndx; i++) {
            if (secondRandomParent == 0) {
                newGenotypes.add(this.genotypes.get(i));
            } else {
                newGenotypes.add(other.genotypes.get(i));
            }
        }
        int thirdRandomParent = random.nextInt(2);
        for (int i = secondRandomIndx; i < 32; i++) {
            if (firstRandomParent == 0 && secondRandomParent == 0) {
                newGenotypes.add(other.genotypes.get(i));
            } else if (firstRandomParent != secondRandomParent) {
                if (thirdRandomParent == 0) {
                    newGenotypes.add(this.genotypes.get(i));
                } else {
                    newGenotypes.add(other.genotypes.get(i));
                }
            } else {
                newGenotypes.add(this.genotypes.get(i));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (newGenotypes.contains(i)) {
                isGen.set(i, true);
            }
        }
        while (isGen.contains(false)) {
            for (int i = 0; i < 8; i++) {
                while (!newGenotypes.contains(i)) {
                    int randomChangeIndx = random.nextInt(32);
                    newGenotypes.set(randomChangeIndx, i);
                    isGen.set(i, true);
                }
            }
            for (int i = 0; i < 8; i++) {
                if (!newGenotypes.contains(i)) {
                    isGen.set(i, false);
                }
            }
        }
        Collections.sort(newGenotypes);
        return newGenotypes;
    }

    public Integer randomGene() {
        Random random = new Random();
        int randomIndx = random.nextInt(32);
        return this.genotypes.get(randomIndx);
    }

    public int getDominantGene() {
        List<Integer> dominantGenes = new ArrayList<Integer>();
        for (int i = 0; i < 8; i++) {
            dominantGenes.add(0);
        }
        for (Integer genotype : this.genotypes) {
            dominantGenes.set(genotype, dominantGenes.get(genotype) + 1);
        }
        int dominantGene = 0;
        int dominantGeneNumber = dominantGenes.get(0);
        for (Integer GeneNumber : dominantGenes) {
            if (GeneNumber > dominantGeneNumber) {
                dominantGeneNumber = GeneNumber;
                dominantGene = dominantGenes.indexOf(dominantGeneNumber);
            }
        }
        return dominantGene;
    }


    public String toString() {
        return "" + this.genotypes;
    }

    public List<Integer> getGenotypes() {
        return this.genotypes;
    }
}
