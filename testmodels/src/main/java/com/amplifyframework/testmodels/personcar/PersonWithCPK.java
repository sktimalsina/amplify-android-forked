/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelPrimaryKey;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;
import com.amplifyframework.core.model.temporal.Temporal;

import java.util.Objects;
import java.util.UUID;

/**
 * This is an autogenerated class representing the Person type in your schema.
 */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Persons", type = Model.Type.USER )
@Index(fields = {"first_name", "age"}, name = "first_name_and_age_based_index")
@Index(fields = {"first_name", "age"}, name = "undefined")
public final class PersonWithCPK implements Model {
    // Constant QueryFields for each property in this model to be used for constructing conditions
    public static final QueryField FIRST_NAME = QueryField.field("first_name");
    public static final QueryField LAST_NAME = QueryField.field("last_name");
    public static final QueryField AGE = QueryField.field("age");
    public static final QueryField DOB = QueryField.field("dob");
    public static final QueryField RELATIONSHIP = QueryField.field("relationship");

    @ModelField(isRequired = true)
    private final String first_name;

    @ModelField(isRequired = true)
    private final String last_name;

    @ModelField(targetType = "Int")
    private final Integer age;

    @ModelField(targetType = "AWSDate")
    private final Temporal.Date dob;

    @ModelField
    private final MaritalStatus relationship;

    @ModelField(targetType = "AWSDateTime", isReadOnly = true)
    private Temporal.DateTime createdAt;

    @ModelField(targetType = "AWSDateTime", isReadOnly = true)
    private Temporal.DateTime updatedAt;

    private PersonPrimaryKey personPrimaryKey;

    private PersonWithCPK(String first_name,
                          String last_name,
                          Integer age,
                          Temporal.Date dob,
                          MaritalStatus relationship) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.age = age;
        this.dob = dob;
        this.relationship = relationship;
    }

    /**
     * Returns an instance of the builder at the first required step.
     * @return an instance of the builder.
     */
    public static FirstNameStep builder() {
        return new Builder();
    }

    /**
     * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
     *
     * This is a convenience method to return an instance of the object with only its ID populated
     * to be used in the context of a parameter in a delete mutation or referencing a foreign key
     * in a relationship.
     * @param id the id of the existing item this instance will represent
     * @return an instance of this model with only ID populated
     */
    public static PersonWithCPK justId(String id) {
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Model IDs must be unique in the format of UUID. This method is for creating instances " +
                    "of an existing object with only its ID field for sending as a mutation parameter. When " +
                    "creating a new object, use the standard builder method and leave the ID field blank."
            );
        }

        return new PersonWithCPK(
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Returns an instance of the pre-set builder to update values with.
     * @return an instance of the pre-set builder to update values with.
     */
    public NewBuilder newBuilder() {
        return new NewBuilder(getPrimaryKeyString(),
                first_name,
                last_name,
                age,
                dob,
                relationship);
    }

    @NonNull
    public PersonPrimaryKey resolveIdentifier() {
        if (personPrimaryKey == null){
            personPrimaryKey = new PersonPrimaryKey(first_name, age);
        }
        return  personPrimaryKey;
    }

    /**
     * Returns the person's first name.
     * @return The person's first name.
     */
    public String getFirstName() {
        return first_name;
    }

    /**
     * Returns the person's last name.
     * @return The person's last name.
     */
    public String getLastName() {
        return last_name;
    }

    /**
     * Returns the person's age.
     * @return The person's age.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Returns the person's date of birth.
     * @return date of birth.
     */
    public Temporal.Date getDob() {
        return dob;
    }

    /**
     * Returns the person's relationship status.
     * @return relationship status.
     */
    public MaritalStatus getRelationship() {
        return relationship;
    }

    /**
     * Returns the DateTime when the model was created.
     * @return the DateTime when the model was created.
     */
    public Temporal.DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the DateTime when the model was last updated.
     * @return the DateTime when the model was last updated.
     */
    public Temporal.DateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if(obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            PersonWithCPK person = (PersonWithCPK) obj;
            return ObjectsCompat.equals(getPrimaryKeyString(), person.getPrimaryKeyString()) &&
                    ObjectsCompat.equals(getAge(), person.getAge()) &&
                    ObjectsCompat.equals(getDob(), person.getDob()) &&
                    ObjectsCompat.equals(getFirstName(), person.getFirstName()) &&
                    ObjectsCompat.equals(getLastName(), person.getLastName()) &&
                    ObjectsCompat.equals(getRelationship(), person.getRelationship()) &&
                    ObjectsCompat.equals(getCreatedAt(), person.getCreatedAt()) &&
                    ObjectsCompat.equals(getUpdatedAt(), person.getUpdatedAt());
        }
    }

    @Override
    public int hashCode() {
        return new StringBuilder()
                .append(getDob())
                .append(getAge())
                .append(getFirstName())
                .append(getLastName())
                .append(getRelationship())
                .append(getCreatedAt())
                .append(getUpdatedAt())
                .toString()
                .hashCode();
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + getPrimaryKeyString() + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", age=" + age +
                ", dob=" + dob +
                ", relationship=" + relationship +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * Interface for required firstName step.
     */
    public interface FirstNameStep {
        /**
         * Set the person's first name.
         * @param firstName The person's first name.
         * @return next step.
         */
        LastNameStep firstName(String firstName);
    }

    /**
     * Interface for lastName step.
     */
    public interface LastNameStep {
        /**
         * Set the person's last name.
         * @param lastName The person's last name.
         * @return next step.
         */
        FinalStep lastName(String lastName);
    }

    /**
     * Interface for final step.
     */
    public interface FinalStep {

        /**
         * Set the person's age.
         * @param age The person's age.
         * @return next step.
         */
        FinalStep age(Integer age);

        /**
         * Set the person's date of birth.
         * @param dob The person's date of birth.
         * @return next step.
         */
        FinalStep dob(Temporal.Date dob);

        /**
         * Set the person's relationship status.
         * @param relationship The person's relationship.
         * @return next step.
         */
        FinalStep relationship(MaritalStatus relationship);

        /**
         * Returns the built Person object.
         * @return the built Person object.
         */
        PersonWithCPK build();
    }

    /**
     * Builder to build the Person object.
     */
    private static class Builder implements
            FirstNameStep, LastNameStep, FinalStep {
        private String first_name;
        private String last_name;
        private Integer age;
        private Temporal.Date dob;
        private MaritalStatus relationship;

        /**
         * Sets the person's first name.
         * @param firstName The person's first name.
         * @return Current Builder instance, for fluent method chaining
         */
        public LastNameStep firstName(String first_name) {
            Objects.requireNonNull(first_name);
            this.first_name = first_name;
            return this;
        }

        /**
         * Sets the person's last name.
         * @param lastName The person's last name.
         * @return Current Builder instance, for fluent method chaining
         */
        public FinalStep lastName(String last_name) {
            Objects.requireNonNull(last_name);
            this.last_name = last_name;
            return this;
        }

        /**
         * Sets the person's age.
         * @param age The person's age
         * @return Current Builder instance, for fluent method chaining
         */
        public FinalStep age(Integer age) {
            this.age = age;
            return this;
        }

        /**
         * Sets the person's date of birth.
         * @param dob The person's date of birth.
         * @return Current Builder instance, for fluent method chaining
         */
        public FinalStep dob(Temporal.Date dob) {
            this.dob = dob == null ? null : new Temporal.Date(dob.toDate());
            return this;
        }

        /**
         * Sets the person's relationship status.
         * @param relationship The person's relationship status.
         * @return Current Builder instance, for fluent method chaining
         */
        public FinalStep relationship(MaritalStatus relationship) {
            this.relationship = relationship;
            return this;
        }

        /**
         * Returns the builder object.
         * @return the builder object.
         */
        public PersonWithCPK build() {

            return new PersonWithCPK(
                    first_name,
                    last_name,
                    age,
                    dob,
                    relationship);
        }
    }

    /**
     * New Builder to update the Person object.
     */
    public final class NewBuilder extends Builder {
        private NewBuilder(String id,
                String first_name,
                String last_name,
                Integer age,
                Temporal.Date dob,
                MaritalStatus relationship) {
            super.firstName(first_name)
                    .lastName(last_name)
                    .age(age)
                    .dob(dob)
                    .relationship(relationship);
        }

        @Override
        public NewBuilder firstName(String first_name) {
            return (NewBuilder) super.firstName(first_name);
        }

        @Override
        public NewBuilder lastName(String last_name) {
            return (NewBuilder) super.lastName(last_name);
        }

        @Override
        public NewBuilder age(Integer age) {
            return (NewBuilder) super.age(age);
        }

        @Override
        public NewBuilder dob(Temporal.Date dob) {
            return (NewBuilder) super.dob(dob);
        }

        @Override
        public NewBuilder relationship(MaritalStatus relationship) {
            return (NewBuilder) super.relationship(relationship);
        }
    }

    public class PersonPrimaryKey extends ModelPrimaryKey<PersonWithCPK> {
        private static final long serialVersionUID = 1L;
        public PersonPrimaryKey(String firstName, int age){
            super(firstName, age);
        }
    }
}
