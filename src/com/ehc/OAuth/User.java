package com.ehc.OAuth;

import com.facebook.model.GraphLocation;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 16/4/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class User implements Serializable {

  private long id;
  private String name;
  String link;
  String userName;
  String birthDay;
  String location;
  String firstName;
  String middleName;
  String lastName;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getBirthDay() {
    return birthDay;
  }

  public void setBirthDay(String birthDay) {
    this.birthDay = birthDay;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

}
