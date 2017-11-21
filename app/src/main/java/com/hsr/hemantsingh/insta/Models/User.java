package com.hsr.hemantsingh.insta.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

//
//	User.java
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport


@RealmClass
public class User extends RealmObject {
	@SerializedName("biography")
	@Expose
	private String biography;
	@SerializedName("country_block")
	@Expose
	private Boolean countryBlock;
	@SerializedName("external_url")
	@Expose
	private String externalUrl;
	@SerializedName("external_url_linkshimmed")
	@Expose
	private String externalUrlLinkshimmed;

	@SerializedName("full_name")
	@Expose
	private String fullName;

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("is_private")
	@Expose
	private Boolean isPrivate;
	@SerializedName("is_verified")
	@Expose
	private Boolean isVerified;
	@SerializedName("profile_pic_url")
	@Expose
	private String profilePicUrl;
	@SerializedName("profile_pic_url_hd")
	@Expose
	private String profilePicUrlHd;

	@SerializedName("username")
	@Expose
	private String username;
	@SerializedName("connected_fb_page")
	@Expose
	private String connectedFbPage;

	private RealmList<String> urls;

	public void setBiography(String biography){
		this.biography = biography;
	}
	public String getBiography(){
		return this.biography;
	}

	public void setConnectedFbPage(String connectedFbPage){
		this.connectedFbPage = connectedFbPage;
	}
	public String getConnectedFbPage(){
		return this.connectedFbPage;
	}

	public void setExternalUrl(String externalUrl){
		this.externalUrl = externalUrl;
	}
	public String getExternalUrl(){
		return this.externalUrl;
	}
	public void setExternalUrlLinkshimmed(String externalUrlLinkshimmed){
		this.externalUrlLinkshimmed = externalUrlLinkshimmed;
	}
	public String getExternalUrlLinkshimmed(){
		return this.externalUrlLinkshimmed;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}
	public String getFullName(){
		return this.fullName;
	}

	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public void setIsPrivate(boolean isPrivate){
		this.isPrivate = isPrivate;
	}
	public boolean isIsPrivate()
	{
		return this.isPrivate;
	}
	public void setIsVerified(boolean isVerified){
		this.isVerified = isVerified;
	}
	public boolean isIsVerified()
	{
		return this.isVerified;
	}

	public void setProfilePicUrl(String profilePicUrl){
		this.profilePicUrl = profilePicUrl;
	}
	public String getProfilePicUrl(){
		return this.profilePicUrl;
	}
	public void setProfilePicUrlHd(String profilePicUrlHd){
		this.profilePicUrlHd = profilePicUrlHd;
	}
	public String getProfilePicUrlHd(){
		return this.profilePicUrlHd;
	}

	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return this.username;
	}

	public RealmList<String> getUrls() {
		return urls;
	}

	public void setUrls(RealmList<String> urls) {
		this.urls = urls;
	}



}