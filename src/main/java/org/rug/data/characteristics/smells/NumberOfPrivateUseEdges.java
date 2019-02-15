package org.rug.data.characteristics.smells;

// Needs to modify arcan in order to retrieve this information

/**
 * (Not implemented) This characteristic computes the number of private usages of the classes affected by this smell.
 */
public class NumberOfPrivateUseEdges extends AbstractSmellCharacteristic {
    /**
     * Sets up the name of this smell characteristic.
     */
    public NumberOfPrivateUseEdges() {
        super("numOfPrivateUseEdges");
    }
}
