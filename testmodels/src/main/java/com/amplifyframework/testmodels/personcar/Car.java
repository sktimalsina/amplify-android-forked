/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.testmodels.personcar;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;

import java.util.UUID;

/**
 * An example of a class that would be generated by Amplify Codegen.
 */
@SuppressWarnings("all")
@ModelConfig
@Index(fields = {"vehicle_model"}, name = "model_based_index")
public final class Car implements Model {

    @ModelField(targetType = "ID", isRequired = true)
    private String id;

    @ModelField(isRequired = true)
    private String vehicle_model;

    @BelongsTo(type = Person.class, targetName = "carOwnerId")
    @ModelField
    private Person owner;

    private Car(String id,
                String vehicle_model,
                Person owner) {
        this.id = id;
        this.vehicle_model = vehicle_model;
        this.owner = owner;
    }

    /**
     * Returns an instance of the builder.
     * @return an instance of the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns Id.
     * @return Id.
     */
    public String resolveIdentifier() {
        return id;
    }

    public String getId() {
        return id;
    }

    /**
     * Return the vehicle model.
     * @return the vehicle model.
     */
    public String getVehicleModel() {
        return vehicle_model;
    }

    /**
     * Return the owner.
     * @return the owner.
     */
    public Person getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Car)) {
            return false;
        }
        Car car = (Car) obj;
        return ObjectsCompat.equals(getId(), car.getId()) &&
                ObjectsCompat.equals(getVehicleModel(), car.getVehicleModel()) &&
                ObjectsCompat.equals(getOwner(), car.getOwner());
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(
                getId(),
                getVehicleModel(),
                getOwner());
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", vehicle_model='" + vehicle_model + '\'' +
                ", owner=" + owner +
                '}';
    }

    /**
     * Interface for vehicle model step.
     */
    public interface VehicleModelStep {
        /**
         * Set the vehicle model.
         * @param vehicle_model vehicle model.
         * @return next step.
         */
        OwnerStep vehicleModel(String vehicle_model);
    }

    /**
     * Interface for personId step.
     */
    public interface OwnerStep {
        /**
         * Set the owner.
         * @param owner owner.
         * @return next step.
         */
        FinalStep owner(Person owner);
    }

    /**
     * Interface for final step.
     */
    public interface FinalStep {
        /**
         * Returns the built Car object.
         * @return the built Car object.
         */
        Car build();
    }

    /**
     * Builder to build the Person object.
     */
    public static final class Builder implements
            VehicleModelStep, OwnerStep, FinalStep {
        private String vehicle_model;
        private Person owner;

        /**
         * Set the vehicle model and proceed to PersonStep.
         * @param vehicle_model vehicle model
         * @return next step
         */
        public OwnerStep vehicleModel(String vehicle_model) {
            this.vehicle_model = vehicle_model;
            return this;
        }

        /**
         * Set the owner and proceed to FinalStep.
         * @param owner owner
         * @return next step
         */
        public FinalStep owner(Person owner) {
            this.owner = owner;
            return this;
        }

        /**
         * Returns the builder object.
         * @return the builder object.
         */
        public Car build() {
            return new Car(
                    UUID.randomUUID().toString(),
                    vehicle_model,
                    owner);
        }
    }
}
