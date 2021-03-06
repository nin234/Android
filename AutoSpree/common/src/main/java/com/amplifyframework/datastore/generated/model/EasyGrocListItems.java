package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the EasyGrocListItems type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "EasyGrocListItems")
@Index(name = "undefined", fields = {"userID","name","masterList"})
public final class EasyGrocListItems implements Model {
  public static final QueryField NAME = field("name");
  public static final QueryField DATE = field("date");
  public static final QueryField MASTER_LIST = field("masterList");
  public static final QueryField USER_ID = field("userID");
  public static final QueryField ADD = field("add");
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String date;
  private final @ModelField(targetType="String", isRequired = true) String masterList;
  private final @ModelField(targetType="String", isRequired = true) String userID;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean add;
  public String getId() {
      return userID;
  }
  
  public String getName() {
      return name;
  }
  
  public String getDate() {
      return date;
  }
  
  public String getMasterList() {
      return masterList;
  }
  
  public String getUserId() {
      return userID;
  }
  
  public Boolean getAdd() {
      return add;
  }
  
  private EasyGrocListItems(String name, String date, String masterList, String userID, Boolean add) {
    this.name = name;
    this.date = date;
    this.masterList = masterList;
    this.userID = userID;
    this.add = add;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      EasyGrocListItems easyGrocListItems = (EasyGrocListItems) obj;
      return       ObjectsCompat.equals(getName(), easyGrocListItems.getName()) &&
              ObjectsCompat.equals(getDate(), easyGrocListItems.getDate()) &&
              ObjectsCompat.equals(getMasterList(), easyGrocListItems.getMasterList()) &&
              ObjectsCompat.equals(getUserId(), easyGrocListItems.getUserId()) &&
              ObjectsCompat.equals(getAdd(), easyGrocListItems.getAdd());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getName())
      .append(getDate())
      .append(getMasterList())
      .append(getUserId())
      .append(getAdd())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("EasyGrocListItems {")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("masterList=" + String.valueOf(getMasterList()) + ", ")
      .append("userID=" + String.valueOf(getUserId()) + ", ")
      .append("add=" + String.valueOf(getAdd()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
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
  public static EasyGrocListItems justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new EasyGrocListItems(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
   */

  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(
      name,
      date,
      masterList,
      userID,
      add);
  }
  public interface NameStep {
    DateStep name(String name);
  }
  

  public interface DateStep {
    MasterListStep date(String date);
  }
  

  public interface MasterListStep {
    UserIdStep masterList(String masterList);
  }
  

  public interface UserIdStep {
    AddStep userId(String userId);
  }
  

  public interface AddStep {
    BuildStep add(Boolean add);
  }
  

  public interface BuildStep {
    EasyGrocListItems build();
   // BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements NameStep, DateStep, MasterListStep, UserIdStep, AddStep, BuildStep {
    private String name;
    private String date;
    private String masterList;
    private String userID;
    private Boolean add;
    @Override
     public EasyGrocListItems build() {

        return new EasyGrocListItems(
          name,
          date,
          masterList,
          userID,
          add);
    }
    
    @Override
     public DateStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public MasterListStep date(String date) {
        Objects.requireNonNull(date);
        this.date = date;
        return this;
    }
    
    @Override
     public UserIdStep masterList(String masterList) {
        Objects.requireNonNull(masterList);
        this.masterList = masterList;
        return this;
    }
    
    @Override
     public AddStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userID = userId;
        return this;
    }
    
    @Override
     public BuildStep add(Boolean add) {
        Objects.requireNonNull(add);
        this.add = add;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
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
     */
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String name, String date, String masterList, String userId, Boolean add) {
      super.name(name)
        .date(date)
        .masterList(masterList)
        .userId(userId)
        .add(add);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder date(String date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder masterList(String masterList) {
      return (CopyOfBuilder) super.masterList(masterList);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder add(Boolean add) {
      return (CopyOfBuilder) super.add(add);
    }
  }
  
}
