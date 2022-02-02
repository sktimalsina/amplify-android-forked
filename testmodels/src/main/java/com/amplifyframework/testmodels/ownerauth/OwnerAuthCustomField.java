package com.amplifyframework.testmodels.ownerauth;


import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the OwnerAuthCustomField type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "OwnerAuthCustomFields", authRules = {
        @AuthRule(allow = AuthStrategy.OWNER),
        @AuthRule(allow = AuthStrategy.OWNER, ownerField = "editors",
                operations = { ModelOperation.CREATE, ModelOperation.READ })
})
public final class OwnerAuthCustomField implements Model {
    public static final QueryField ID = field("id");
    public static final QueryField TITLE = field("title");
    public static final QueryField EDITORS = field("editors");
    private final @ModelField(targetType="ID", isRequired = true) String id;
    private final @ModelField(targetType="String", isRequired = true) String title;
    private final @ModelField(targetType="String") List<String> editors;
    public String resolveIdentifier() {
        return id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getEditors() {
        return editors;
    }

    private OwnerAuthCustomField(String id, String title, List<String> editors) {
        this.id = id;
        this.title = title;
        this.editors = editors;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if(obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            OwnerAuthCustomField ownerAuthCustomField = (OwnerAuthCustomField) obj;
            return ObjectsCompat.equals(getId(), ownerAuthCustomField.getId()) &&
                    ObjectsCompat.equals(getTitle(), ownerAuthCustomField.getTitle()) &&
                    ObjectsCompat.equals(getEditors(), ownerAuthCustomField.getEditors());
        }
    }

    @Override
    public int hashCode() {
        return new StringBuilder()
                .append(getId())
                .append(getTitle())
                .append(getEditors())
                .toString()
                .hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("OwnerAuthCustomField {")
                .append("id=" + String.valueOf(getId()) + ", ")
                .append("title=" + String.valueOf(getTitle()) + ", ")
                .append("editors=" + String.valueOf(getEditors()))
                .append("}")
                .toString();
    }

    public static TitleStep builder() {
        return new Builder();
    }

    /**
     * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
     * This is a convenience method to return an instance of the object with only its ID populated
     * to be used in the context of a parameter in a delete mutation or referencing a foreign key
     * in a relationship.
     * @param id the id of the existing item this instance will represent
     * @return an instance of this model with only ID populated
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public static OwnerAuthCustomField justId(String id) {
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Model IDs must be unique in the format of UUID. This method is for creating instances " +
                            "of an existing object with only its ID field for sending as a mutation parameter. When " +
                            "creating a new object, use the standard builder method and leave the ID field blank."
            );
        }
        return new OwnerAuthCustomField(
                id,
                null,
                null
        );
    }

    public CopyOfBuilder copyOfBuilder() {
        return new CopyOfBuilder(id,
                title,
                editors);
    }
    public interface TitleStep {
        BuildStep title(String title);
    }


    public interface BuildStep {
        OwnerAuthCustomField build();
        BuildStep id(String id) throws IllegalArgumentException;
        BuildStep editors(List<String> editors);
    }


    public static class Builder implements TitleStep, BuildStep {
        private String id;
        private String title;
        private List<String> editors;
        @Override
        public OwnerAuthCustomField build() {
            String id = this.id != null ? this.id : UUID.randomUUID().toString();

            return new OwnerAuthCustomField(
                    id,
                    title,
                    editors);
        }

        @Override
        public BuildStep title(String title) {
            Objects.requireNonNull(title);
            this.title = title;
            return this;
        }

        @Override
        public BuildStep editors(List<String> editors) {
            this.editors = editors;
            return this;
        }

        /**
         * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
         * This should only be set when referring to an already existing object.
         * @param id id
         * @return Current Builder instance, for fluent method chaining
         * @throws IllegalArgumentException Checks that ID is in the proper format
         */
        public BuildStep id(String id) throws IllegalArgumentException {
            this.id = id;

            try {
                UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
            } catch (Exception exception) {
                throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                        exception);
            }

            return this;
        }
    }


    public final class CopyOfBuilder extends Builder {
        private CopyOfBuilder(String id, String title, List<String> editors) {
            super.id(id);
            super.title(title)
                    .editors(editors);
        }

        @Override
        public CopyOfBuilder title(String title) {
            return (CopyOfBuilder) super.title(title);
        }

        @Override
        public CopyOfBuilder editors(List<String> editors) {
            return (CopyOfBuilder) super.editors(editors);
        }
    }

}
