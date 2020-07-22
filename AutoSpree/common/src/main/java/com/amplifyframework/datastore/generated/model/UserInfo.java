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

/** This is an auto generated class representing the UserInfo type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserInfos")
@Index(name = "undefined", fields = {"userID"})
public final class UserInfo implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField SHARE_ID = field("share_id");
  public static final QueryField DATE = field("date");
  public static final QueryField USER_ID = field("userID");
  public static final QueryField VERIFIED = field("verified");
  public static final QueryField PLACEHOLDER1 = field("placeholder1");
  public static final QueryField PLACEHOLDER2 = field("placeholder2");
  public static final QueryField PLACEHOLDER3 = field("placeholder3");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Int", isRequired = true) Integer share_id;
  private final @ModelField(targetType="String", isRequired = true) String date;
  private final @ModelField(targetType="String", isRequired = true) String userID;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean verified;
  private final @ModelField(targetType="String") String placeholder1;
  private final @ModelField(targetType="String") String placeholder2;
  private final @ModelField(targetType="String") String placeholder3;
  public String getId() {
      return id;
  }
  
  public Integer getShareId() {
      return share_id;
  }
  
  public String getDate() {
      return date;
  }
  
  public String getUserId() {
      return userID;
  }
  
  public Boolean getVerified() {
      return verified;
  }
  
  public String getPlaceholder1() {
      return placeholder1;
  }
  
  public String getPlaceholder2() {
      return placeholder2;
  }
  
  public String getPlaceholder3() {
      return placeholder3;
  }
  
  private UserInfo(String id, Integer share_id, String date, String userID, Boolean verified, String placeholder1, String placeholder2, String placeholder3) {
    this.id = id;
    this.share_id = share_id;
    this.date = date;
    this.userID = userID;
    this.verified = verified;
    this.placeholder1 = placeholder1;
    this.placeholder2 = placeholder2;
    this.placeholder3 = placeholder3;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserInfo userInfo = (UserInfo) obj;
      return ObjectsCompat.equals(getId(), userInfo.getId()) &&
              ObjectsCompat.equals(getShareId(), userInfo.getShareId()) &&
              ObjectsCompat.equals(getDate(), userInfo.getDate()) &&
              ObjectsCompat.equals(getUserId(), userInfo.getUserId()) &&
              ObjectsCompat.equals(getVerified(), userInfo.getVerified()) &&
              ObjectsCompat.equals(getPlaceholder1(), userInfo.getPlaceholder1()) &&
              ObjectsCompat.equals(getPlaceholder2(), userInfo.getPlaceholder2()) &&
              ObjectsCompat.equals(getPlaceholder3(), userInfo.getPlaceholder3());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getShareId())
      .append(getDate())
      .append(getUserId())
      .append(getVerified())
      .append(getPlaceholder1())
      .append(getPlaceholder2())
      .append(getPlaceholder3())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserInfo {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("share_id=" + String.valueOf(getShareId()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("userID=" + String.valueOf(getUserId()) + ", ")
      .append("verified=" + String.valueOf(getVerified()) + ", ")
      .append("placeholder1=" + String.valueOf(getPlaceholder1()) + ", ")
      .append("placeholder2=" + String.valueOf(getPlaceholder2()) + ", ")
      .append("placeholder3=" + String.valueOf(getPlaceholder3()))
      .append("}")
      .toString();
  }
  
  public static ShareIdStep builder() {
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
  public static UserInfo justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new UserInfo(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      share_id,
      date,
      userID,
      verified,
      placeholder1,
      placeholder2,
      placeholder3);
  }
  public interface ShareIdStep {
    DateStep shareId(Integer shareId);
  }
  

  public interface DateStep {
    UserIdStep date(String date);
  }
  

  public interface UserIdStep {
    VerifiedStep userId(String userId);
  }
  

  public interface VerifiedStep {
    BuildStep verified(Boolean verified);
  }
  

  public interface BuildStep {
    UserInfo build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep placeholder1(String placeholder1);
    BuildStep placeholder2(String placeholder2);
    BuildStep placeholder3(String placeholder3);
  }
  

  public static class Builder implements ShareIdStep, DateStep, UserIdStep, VerifiedStep, BuildStep {
    private String id;
    private Integer share_id;
    private String date;
    private String userID;
    private Boolean verified;
    private String placeholder1;
    private String placeholder2;
    private String placeholder3;
    @Override
     public UserInfo build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserInfo(
          id,
          share_id,
          date,
          userID,
          verified,
          placeholder1,
          placeholder2,
          placeholder3);
    }
    
    @Override
     public DateStep shareId(Integer shareId) {
        Objects.requireNonNull(shareId);
        this.share_id = shareId;
        return this;
    }
    
    @Override
     public UserIdStep date(String date) {
        Objects.requireNonNull(date);
        this.date = date;
        return this;
    }
    
    @Override
     public VerifiedStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userID = userId;
        return this;
    }
    
    @Override
     public BuildStep verified(Boolean verified) {
        Objects.requireNonNull(verified);
        this.verified = verified;
        return this;
    }
    
    @Override
     public BuildStep placeholder1(String placeholder1) {
        this.placeholder1 = placeholder1;
        return this;
    }
    
    @Override
     public BuildStep placeholder2(String placeholder2) {
        this.placeholder2 = placeholder2;
        return this;
    }
    
    @Override
     public BuildStep placeholder3(String placeholder3) {
        this.placeholder3 = placeholder3;
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
    private CopyOfBuilder(String id, Integer shareId, String date, String userId, Boolean verified, String placeholder1, String placeholder2, String placeholder3) {
      super.id(id);
      super.shareId(shareId)
        .date(date)
        .userId(userId)
        .verified(verified)
        .placeholder1(placeholder1)
        .placeholder2(placeholder2)
        .placeholder3(placeholder3);
    }
    
    @Override
     public CopyOfBuilder shareId(Integer shareId) {
      return (CopyOfBuilder) super.shareId(shareId);
    }
    
    @Override
     public CopyOfBuilder date(String date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder verified(Boolean verified) {
      return (CopyOfBuilder) super.verified(verified);
    }
    
    @Override
     public CopyOfBuilder placeholder1(String placeholder1) {
      return (CopyOfBuilder) super.placeholder1(placeholder1);
    }
    
    @Override
     public CopyOfBuilder placeholder2(String placeholder2) {
      return (CopyOfBuilder) super.placeholder2(placeholder2);
    }
    
    @Override
     public CopyOfBuilder placeholder3(String placeholder3) {
      return (CopyOfBuilder) super.placeholder3(placeholder3);
    }
  }
  
}
