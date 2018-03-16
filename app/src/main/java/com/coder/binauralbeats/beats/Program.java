package com.coder.binauralbeats.beats;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * @author Giorgio Regni
 * @contact @GiorgioRegni on Twitter
 * http://twitter.com/GiorgioRegni
 *
 * This file is part of Binaural Beats Therapy or BBT.
 *
 *   BBT is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   BBT is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with BBT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   BBT project home is at https://github.com/GiorgioRegni/Binaural-Beats
 */

public class Program implements Parcelable {

	protected String name;
	protected String description;
	protected ArrayList<Period> seq;
	private String author = "@GiorgioRegni";
	boolean useGL = false;

	public Program(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		description = d;
	}

	public Program addPeriod(Period p) {
		if (seq==null){
			seq=new ArrayList<>();
		}
		seq.add(p);
		return this;
	}

	public Iterator<Period> getPeriodsIterator() {
		return seq.iterator();
	}

	public int getLength() {
		int len = 0;

		for (Period p: seq) {
			len += p.getLength();
		}

		return len;
	}

	public int getNumPeriods() {
		return seq.size();
	}

	public void setAuthor(String name) {
		author  = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setGL() {
		useGL = true;
	}

	public boolean doesUseGL() {
		return useGL;
	}



	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.description);
		dest.writeList(this.seq);
		dest.writeString(this.author);
		dest.writeByte(this.useGL ? (byte) 1 : (byte) 0);
	}

	protected Program(Parcel in) {
		this.name = in.readString();
		this.description = in.readString();
		this.seq = new ArrayList<Period>();
		in.readList(this.seq, Period.class.getClassLoader());
		this.author = in.readString();
		this.useGL = in.readByte() != 0;
	}

	public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {
		@Override
		public Program createFromParcel(Parcel source) {
			return new Program(source);
		}

		@Override
		public Program[] newArray(int size) {
			return new Program[size];
		}
	};
}
