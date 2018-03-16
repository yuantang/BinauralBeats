package com.coder.binauralbeats.beats;
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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Period implements Parcelable {
	/**
	 * Length of this period in second
	 */
	public int length;
	public ArrayList<BinauralBeatVoice> voices;

	public SoundLoop background;
	private float backgroundvol;

	Visualization v;

	boolean strechable;

	public Period(int length,  SoundLoop background,
				  float backgroundvol, Visualization v) {
		super();
		this.length = length;
		this.background = background;
		this.setBackgroundvol(backgroundvol);
		this.v = v;
		this.strechable = false;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public SoundLoop getBackground() {
		return background;
	}

	public void setBackground(SoundLoop background) {
		this.background = background;
	}

	public void setBackgroundvol(float backgroundvol) {
		this.backgroundvol = backgroundvol;
	}

	public float getBackgroundvol() {
		return backgroundvol;
	}

	public Visualization getV() {
		return v;
	}

	public Period setV(Visualization v) {
		this.v = v;
		return this;
	}

	public ArrayList<BinauralBeatVoice> getVoices() {
		return voices;
	}

	public void setVoices(ArrayList<BinauralBeatVoice> voices) {
		this.voices = voices;
	}

	public Period addVoice(BinauralBeatVoice v) {
		if (voices == null)
			voices = new ArrayList<>();
		voices.add(v);
		return this;
	}

	public Period addHarmonicBox(float freq, float pitch, float volume) {

		BinauralBeatVoice v1 = new BinauralBeatVoice(freq, freq, volume, pitch);
		BinauralBeatVoice v2 = new BinauralBeatVoice(-freq, -freq, volume, pitch - freq*2);

		addVoice(v1);
		addVoice(v2);

		return this;
	}


	public boolean isStrechable() {
		return strechable;
	}

	public Period setStrechable() {
		this.strechable = true;
		return this;
	}

	public float getMainBeatStart() {
		return voices.get(0).freqStart;
	}
	public float getMainBeatEnd() {
		return voices.get(0).freqEnd;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.length);
		dest.writeTypedList(this.voices);
		dest.writeInt(this.background == null ? -1 : this.background.ordinal());
		dest.writeFloat(this.backgroundvol);
		dest.writeParcelable((Parcelable) this.v, flags);
		dest.writeByte(this.strechable ? (byte) 1 : (byte) 0);
	}

	protected Period(Parcel in) {
		this.length = in.readInt();
		this.voices = in.createTypedArrayList(BinauralBeatVoice.CREATOR);
		int tmpBackground = in.readInt();
		this.background = tmpBackground == -1 ? null : SoundLoop.values()[tmpBackground];
		this.backgroundvol = in.readFloat();
		this.v = in.readParcelable(Visualization.class.getClassLoader());
		this.strechable = in.readByte() != 0;
	}

	public static final Parcelable.Creator<Period> CREATOR = new Parcelable.Creator<Period>() {
		@Override
		public Period createFromParcel(Parcel source) {
			return new Period(source);
		}

		@Override
		public Period[] newArray(int size) {
			return new Period[size];
		}
	};
}
