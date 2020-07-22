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

/** This is an auto generated class representing the AccountLink type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "AccountLinks")
@Index(name = "undefined", fields = {"code"})
public final class AccountLink implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField CODE = field("code");
  public static final QueryField DATE = field("date");
  public static final QueryField USER_ID = field("userID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Int", isRequired = true) Integer code;
  private final @ModelField(targetType="String", isRequired = true) String date;
  private final @ModelField(targetType="String", isRequired = true) String userID;
  public String getId() {
      return id;
  }
  
  public Integer getCode() {
      return code;
  }
  
  public String getDate() {
      return date;
  }
  
  public String getUserId() {
      return userID;
  }
  
  private AccountLink(String id, Integer code, String date, String userID) {
    this.id = id;
    this.code = code;
    this.date = date;
    this.userID = userID;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      AccountLink accountLink = (AccountLink) obj;
      return ObjectsCompat.equals(getId(), accountLink.getId()) &&
              ObjectsCompat.equals(getCode(), accountLink.getCode()) &&
              ObjectsCompat.equals(getDate(), accountLink.getDate()) &&
              ObjectsCompat.equals(getUserId(), accountLink.getUserId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getCode())
      .append(getDate())
      .append(getUserId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("AccountLink {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("code=" + String.valueOf(getCode()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("userID=" + String.valueOf(getUserId()))
      .append("}")
      .toString();
  }
  
  public static CodeStep builder() {
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
  public static AccountLink justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new AccountLink(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      code,
      date,
      userID);
  }
  public interface CodeStep {
    DateStep code(Integer code);
  }
  

  public interface DateStep {
    UserIdStep date(String date);
  }
  

  public interface UserIdStep {
    BuildStep userId(String userId);
  }
  

  public interface BuildStep {
    AccountLink build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements CodeStep, DateStep, UserIdStep, BuildStep {
    private String id;
    private Integer code;
    private String date;
    private String userID;
    @Override
     public AccountLink build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new AccountLink(
          id,
          code,
          date,
          userID);
    }
    
    @Override
     public DateStep code(Integer code) {
        Objects.requireNonNull(code);
        this.code = code;
        return this;
    }
    
    @Override
     public UserIdStep date(String date) {
        Objects.requireNonNull(date);
        this.date = date;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userID = userId;
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
    private CopyOfBuilder(String id, Integer code, String date, String userId) {
      super.id(id);
      super.code(code)
        .date(date)
        .userId(userId);
    }
    
    @Override
     public CopyOfBuilder code(Integer code) {
      return (CopyOfBuilder) super.code(code);
    }
    
    @Override
     public CopyOfBuilder date(String date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
  }
  
}
