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

public class BinauralBeatVoice implements Parcelable {
	public static final float DEFAULT = -1f;

	/**
	 * Beat frequency in Hz
	 */
    public float freqStart;
	public float freqEnd;
	
	/**
	 * Beat volume 0f-1f
	 */
	public float volume;
	
	/**
	 * 440 = default
	 */
	public float pitch;
	public boolean isochronic;

	public BinauralBeatVoice(float freqStart, float freqEnd, float volume, float pitch) {
		this.freqStart = freqStart;
		this.freqEnd = freqEnd;
		this.volume = volume;
		this.pitch = pitch;
		this.isochronic = false;
	}

	public BinauralBeatVoice(float freqStart, float freqEnd, float volume) {
		this(freqStart, freqEnd, volume, DEFAULT);
	}	

	public BinauralBeatVoice setIsochronic(boolean yesno) {
		isochronic = yesno;
		return this;
	}
	
	public boolean isIsochronic() {
		return isochronic;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(this.freqStart);
		dest.writeFloat(this.freqEnd);
		dest.writeFloat(this.volume);
		dest.writeFloat(this.pitch);
		dest.writeByte(this.isochronic ? (byte) 1 : (byte) 0);
	}

	protected BinauralBeatVoice(Parcel in) {
		this.freqStart = in.readFloat();
		this.freqEnd = in.readFloat();
		this.volume = in.readFloat();
		this.pitch = in.readFloat();
		this.isochronic = in.readByte() != 0;
	}

	public static final Parcelable.Creator<BinauralBeatVoice> CREATOR = new Parcelable.Creator<BinauralBeatVoice>() {
		@Override
		public BinauralBeatVoice createFromParcel(Parcel source) {
			return new BinauralBeatVoice(source);
		}

		@Override
		public BinauralBeatVoice[] newArray(int size) {
			return new BinauralBeatVoice[size];
		}
	};
}
