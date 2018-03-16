package com.coder.binauralbeats.beats;

import com.coder.binauralbeats.utils.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryGroup implements Serializable{
	private String groupName;
	private String niceName;
	private ArrayList<ProgramMeta> objets;

	public CategoryGroup(String name) {
		super();
		this.groupName = name;
		this.objets = new ArrayList<ProgramMeta>();
	}

	public String getName() {
		return groupName;
	}

	public void setName(String nom) {
		this.groupName = nom;
	}

	public ArrayList<ProgramMeta> getObjets() {
		return objets;
	}
	
	public void add(ProgramMeta m) {
		m.setGroup(this);
		objets.add(m);
	}

	public void setObjets(ArrayList<ProgramMeta> objets) {
		this.objets = objets;
	}

	public String getNiceName() {
		if (niceName == null)
			niceName = WordUtils.capitalize(groupName.toLowerCase());
		return niceName;
	}

	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}
}
