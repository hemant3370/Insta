package com.hsr.hemantsingh.insta;//
//	Image.java
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import org.json.*;
import io.realm.*;
import io.realm.annotations.*;


@RealmClass
public class Image extends RealmObject{

	private LowResolution lowResolution;
	private LowResolution standard_resolution;
	private LowResolution thumbnail;

	public void setLowResolution(LowResolution lowResolution){
		this.lowResolution = lowResolution;
	}
	public LowResolution getLowResolution(){
		return this.lowResolution;
	}
	public void setStandard_resolution(LowResolution standard_resolution){
		this.standard_resolution = standard_resolution;
	}
	public LowResolution getStandard_resolution(){
		return this.standard_resolution;
	}
	public void setThumbnail(LowResolution thumbnail){
		this.thumbnail = thumbnail;
	}
	public LowResolution getThumbnail(){
		return this.thumbnail;
	}

	/**
	 * Creates instance using the passed realm and jsonObject to set the properties values
	 */
	public static Image fromJson(Realm realm, JSONObject jsonObject){
		if(jsonObject == null){
			return null;
		}
		Image image = realm.createObject(Image.class);
		image.lowResolution = LowResolution.fromJson(realm, jsonObject.optJSONObject("low_resolution"));
		image.standard_resolution = LowResolution.fromJson(realm, jsonObject.optJSONObject("standard_resolution"));
		image.thumbnail = LowResolution.fromJson(realm, jsonObject.optJSONObject("thumbnail"));
		return image;
	}

	/**
	 * Returns all the available property values in the form of JSONObject instance where the key is the approperiate json key and the value is the value of the corresponding field
	 */
	public static JSONObject toJsonObject(Image image)
	{
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("low_resolution", LowResolution.toJsonObject(image.lowResolution));
			jsonObject.put("standard_resolution", LowResolution.toJsonObject(image.standard_resolution));
			jsonObject.put("thumbnail", LowResolution.toJsonObject(image.thumbnail));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

}