package com.dhy.coffeesecret.pojo;

import java.io.Serializable;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/23.
 */

public class Species implements Serializable{
    // 咖啡小类
    private String species;
    // 咖啡大类
    private String oneSpecies;

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getOneSpecies() {
        return oneSpecies;
    }

    public void setOneSpecies(String oneSpecies) {
        this.oneSpecies = oneSpecies;
    }

    @Override
    public String toString() {
        return "Species{" +
                "species='" + species + '\'' +
                ", oneSpecies='" + oneSpecies + '\'' +
                '}';
    }
}
