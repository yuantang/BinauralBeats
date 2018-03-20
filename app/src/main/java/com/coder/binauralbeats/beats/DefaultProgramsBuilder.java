package com.coder.binauralbeats.beats;

/*
 * @author Giorgio Regni
 * @c ontact @GiorgioRegni on Twitter
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

import android.content.Context;
import android.util.Log;

import com.coder.binauralbeats.MyApp;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.utils.WordUtils;
import com.coder.binauralbeats.viz.Aurora;
import com.coder.binauralbeats.viz.Black;
import com.coder.binauralbeats.viz.Flash;
import com.coder.binauralbeats.viz.Hiit;
import com.coder.binauralbeats.viz.HypnoFlash;
import com.coder.binauralbeats.viz.Image;
import com.coder.binauralbeats.viz.LSD;
import com.coder.binauralbeats.viz.Leds;
import com.coder.binauralbeats.viz.Morphine;
import com.coder.binauralbeats.viz.None;
import com.coder.binauralbeats.viz.Starfield;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class DefaultProgramsBuilder {
	/*
	Meditation/Relaxation
	Presets aimed at inducing a meditative and/or relaxed state of mind.
	Sleep/Dreams
	Presets related to sleeping. This includes sleep induction, sleep reduction, dream manipulation, etc.
	Treatment/Healing
	Presets aimed at treating a certain physical or mental condition, and/or healing the body/soul in one way or another.
	Hypnosis/Subliminal
	Presets meant for hypnosis, or to make the mind open for suggestions (also known as subliminal programming).
	Focus/Alertness
	Presets aimed at focusing the mind, and/or increase alertness.
	Stimulation/Chakras
	Presets aimed at stimulating certain parts of the body/soul. This includes presets related to different chakras.
	Out-of-body experiences
	*/

	private static final String TAG = null;
	private static Map<String, ProgramMeta> names = null;

	public static Map<String,ProgramMeta> getProgramMethods(Context context) {
		if (names != null)
			return names;
		names = new HashMap<>();
		Class<R.string> resourceStrings = R.string.class;
		for (Method method : DefaultProgramsBuilder.class.getMethods()) {
			if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().isAssignableFrom(Program.class) && method.getName().matches("[A-Z0-9_]+")) {
				try {
					ProgramMeta.Category cat = getMatchingCategory(method.getName());
					String parsedMethod = method.getName().toLowerCase().substring(cat.toString().length()+1);
					String string_res = "program_"+parsedMethod;
					String nice_name;
					try {
						nice_name = context.getString(resourceStrings.getField(string_res).getInt(null));
					} catch (NoSuchFieldException e) {
						Log.w(TAG, String.format("Missing string for %s", parsedMethod));
						nice_name = WordUtils.capitalize(parsedMethod);
					}
					ProgramMeta meta = new ProgramMeta(method, nice_name, cat);

					names.put(nice_name, meta);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return names;
	}

	public static Program getProgram(ProgramMeta pm) {
		try {
			return (Program) pm.getMethod().invoke(null, new Program(pm.getName()));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * First word of a program name is used as a category from ProgramMeta.java
	 */

	public static Program HYPNOSIS_SELF_HYPNOSIS(Program p) {
//		p.setDescription("Short meditation preset to unite your conscious and subconscious mind.It lasts 20mn : Glide down to theta waves, plateau for 10mn then then slowly come back up to awake state.");
		p.setDescription(MyApp.getInstance().getString(R.string.program_self_hypnosis_desc));
		p.setAuthor("@GiorgioRegni");

		p.addPeriod(new Period(300, SoundLoop.WHITE_NOISE, 0.3f, null).
				addVoice(new BinauralBeatVoice(12f, 4f, 0.6f)).
				setV(new HypnoFlash())
		).
				addPeriod(new Period(600, SoundLoop.WHITE_NOISE, 0.3f, null).
						addVoice(new BinauralBeatVoice(4f, 4f, 0.65f)).
						setV(new HypnoFlash())
				).
				addPeriod(new Period(300, SoundLoop.WHITE_NOISE, 0.3f, null).
						addVoice(new BinauralBeatVoice(4f, 12f, 0.6f)).
						setV(new HypnoFlash())
				);

		return p;
	}

	public static Program STIMULATION_HIGHEST_MENTAL_ACTIVITY(Program p) {
//		p.setDescription("A quick cafeine boost ! This preset shortly reaches Gamma waves, provides higher mental activity, including perception, problem solving, and consciousness.It can be used whenever needed");
		p.setDescription(MyApp.getInstance().getString(R.string.program_highest_mental_activity_desc));
			p.setAuthor("@GiorgioRegni");

		p.addPeriod(new Period(120,SoundLoop.WHITE_NOISE, 0.3f, null).
				addVoice( new BinauralBeatVoice(12f, 70f, 0.60f)).
				setV(new Leds())).
				addPeriod(new Period(600,SoundLoop.WHITE_NOISE, 0.3f, null).
						addVoice( new BinauralBeatVoice(70f, 50f, 0.65f)).
						setV(new Leds())
				).
				addPeriod(new Period(120,SoundLoop.WHITE_NOISE, 0.3f, null).
						addVoice( new BinauralBeatVoice(50f, 12f, 0.60f)).
						setV(new Leds())
				);

		return p;
	}

	public static Program MEDITATION_UNITY(Program p) {
		p.setDescription(MyApp.getInstance().getString(R.string.program_unity_desc));
//		p.setDescription("Wander in deep relaxing delta waves and let your mind explore freely and without bounds. May induce dreamless sleep and loss of body awareness.");
		p.setAuthor("@GiorgioRegni");

		p.addPeriod(new Period(3600,SoundLoop.UNITY,  0.5f, null).
				addVoice( new BinauralBeatVoice(3.7f, 3.7f, 0.6f)).
				addVoice( new BinauralBeatVoice(2.5f, 2.5f, 0.6f)).
				addVoice( new BinauralBeatVoice(5.9f, 5.9f, 0.6f)).
				setV(new Aurora())
		);

		return p;
	}

	public static Program HEALING_MORPHINE(Program p) {
		p.setDescription(MyApp.getInstance().getString(R.string.program_morphine_desc));
//		p.setDescription("A relaxing, southing mix of beats that slowly but surely appease any pain point. The brain runs the body as you will discover with this preset.");
		p.setAuthor("@GiorgioRegni");

		// From http://www.bwgen.com/presets/desc263.htm
		p.addPeriod(new Period(3600,SoundLoop.UNITY, 0.5f, null).
				addVoice( new BinauralBeatVoice(15f, 0.5f, 0.5f)).
				addVoice( new BinauralBeatVoice(10f, 10f, 0.5f)).
				addVoice( new BinauralBeatVoice(9f, 9f, 0.5f)).
				addVoice( new BinauralBeatVoice(7.5f, 7.5f, 0.50f)).
				addVoice( new BinauralBeatVoice(38f, 38f, 0.50f)).
				setV(new Morphine())
		);

		return p;
	}

	public static Program LEARNING_LEARNING(Program p) {
		p.setDescription(MyApp.getInstance().getString(R.string.program_learning_desc));
//		p.setDescription("A 2h preset that enhances learning, increases ability to concentrate and think clearly, reduces unwillingness to work. Students can't get enough of this program! This one is to be listened while studying. ");
		p.setAuthor("@GiorgioRegni");
		p.addPeriod(new Period(120,SoundLoop.NONE, 0.6f, null).
				addVoice( new BinauralBeatVoice(60f, 14f, 0.60f)).
				addVoice( new BinauralBeatVoice(70f, 22f, 0.50f)).
				setV(new Flash()))
				.addPeriod(new Period(7200-120,SoundLoop.NONE,  0.6f, null).
						addVoice( new BinauralBeatVoice(14f, 14f, 0.6f)).
						addVoice( new BinauralBeatVoice(22f, 22f, 0.5f)).
						addVoice( new BinauralBeatVoice(12f, 12f, 0.4f)).
						addVoice( new BinauralBeatVoice(6f, 6f, 0.4f)).
						setV(new Flash())
				);

		return p;
	}

	public static Program STIMULATION_CREATIVITY(Program p) {
		p.setDescription(MyApp.getInstance().getString(R.string.program_creativity_desc));
//		p.setDescription("It^s a 20mn meditation preset to assist in Creative Thinking It begins at 10hz then varying from 8 to 6 hz with a glide back to 8hz at the end");
		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(15,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(10f, 8f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(240,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(8f, 8f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(15,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(8f, 6f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(240,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(6f, 6f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(15,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(6f, 8f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(555,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(8f, 8f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(15,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(8f, 6f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(75,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(6f, 6f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(15,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(6f, 8f, 0.6f)).
				setV(new Flash())
		).addPeriod(new Period(45,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(8f, 8f, 0.6f)).
				setV(new Flash())
		);

		return p;
	}


	public static Program MEDITATION_WAKEFULRELAX(Program p) {
		Visualization v = new None();
		p.setDescription(MyApp.getInstance().getString(R.string.program_wakefulrelax_desc));
//		p.setDescription("This presets stimulates a wakeful relaxation state with closed eyes. Zen-trained meditation masters produce noticeably more alpha waves during meditation.To be used in short 10 to 15 minutes doses to calm down and concentrate when needed.");

		p.setAuthor("@GiorgioRegni");

		p.addPeriod(new Period(120,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(12f,	7.83f, 0.6f)).
				addVoice( new BinauralBeatVoice(12f,	7.83f, 0.4f)).
				addVoice( new BinauralBeatVoice(12f,	10f, 0.4f)).
				setV(v))
				.addPeriod(new Period(900-120,SoundLoop.WHITE_NOISE, 0.4f, null).
						addVoice( new BinauralBeatVoice(7.83f, 7.83f, 0.6f)).
						addVoice( new BinauralBeatVoice(7.83f, 7.83f, 0.4f)).
						addVoice( new BinauralBeatVoice(10f, 10f, 0.4f)).
						setV(v)
				);

		return p;
	};



	public static Program MEDITATION_SCHUMANN_RESONANCE(Program p) {
		Visualization v = new Image(R.drawable.warp);
		p.setDescription(MyApp.getInstance().getString(R.string.program_schumann_resonance_desc));
//		p.setDescription("A meditation to put your mind in balance with the Earth. Glide down to Schumann Resonance, low frequency portion of the Earth's electromagnetic field, resonate for 10 minutes then slowly come back up to awake state.");

		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(12f,	7.83f, 0.6f)).
				setV(v))
				.addPeriod(new Period(600,SoundLoop.WHITE_NOISE, 0.4f, null).
						addVoice(new BinauralBeatVoice(7.83f, 7.83f, 0.65f)).
						setV(v))
				.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.4f, null).
						addVoice(new BinauralBeatVoice(7.83f, 12f, 0.6f)).
						setV(v)
				);

		return p;
	}

	public static Program OOBE_ASTRAL_01_RELAX(Program p) {
//		p.setDescription("From the book Mastering Astral Projection, Week 1: Relaxation. Use a low to medium sound level, sit in a quiet place, and listen to the preset with eyes closed. A hard-backed chair without neck support is recommended to prevent falling asleep.");
		p.setDescription(MyApp.getInstance().getString(R.string.program_astral_01_relax_desc));
		p.setAuthor("@GiorgioRegni");
		p.addPeriod(new Period(600,SoundLoop.WHITE_NOISE, 0.4f, null).
				addVoice( new BinauralBeatVoice(15f,	10f, 0.65f)).
				addVoice( new BinauralBeatVoice(15f,	10f, 0.65f)).
				addVoice( new BinauralBeatVoice(15f,	10f, 0.65f)).
				setV(new Starfield()))
				.addPeriod(new Period(60*80,SoundLoop.WHITE_NOISE, 0.4f, null).
						addVoice( new BinauralBeatVoice(10f, 10f, 0.60f)).
						addVoice( new BinauralBeatVoice(10f, 10f, 0.60f)).
						addVoice( new BinauralBeatVoice(10f, 10f, 0.60f)).
						addVoice( new BinauralBeatVoice(4.5f, 2.5f, 0.60f)).
						setV(new Starfield())
				);

		return p;
	}

	public static Program STIMULATION_HIIT(Program p) {
		Hiit v = new Hiit();
		p.setDescription(MyApp.getInstance().getString(R.string.program_hiit_desc));
//		p.setDescription("This preset is based on a Tabata HIIT protocol, it helps you give your maximum during work periods and recover quicker during rest periods.Starts with 2 minutes warm up then 8 times 20 secs work, 10 secs rest followed by 2 minutes  of cool down. As always use headphones.");
		p.setAuthor("@GiorgioRegni");
		p.addPeriod(new Period(120,SoundLoop.NONE, 0.2f, null).
				addVoice( new BinauralBeatVoice(20f, 70f, 0.65f)).
				addVoice( new BinauralBeatVoice(20f, 50f, 0.55f)).
				setV(new Image(R.drawable.hiit_warmup)));

		for (int i=0; i<8;i++)
			p.addPeriod(new Period(20,SoundLoop.NONE, 0.2f, null).
					addVoice( new BinauralBeatVoice(70f, 70f, 0.65f)).
					addVoice( new BinauralBeatVoice(70f, 50f, 0.55f)).
					setV(v))
					.addPeriod(new Period(10,SoundLoop.NONE, 0.2f, null).
							addVoice( new BinauralBeatVoice(8f, 8f, 0.65f)).
							addVoice( new BinauralBeatVoice(50f, 70f, 0.55f)).
							setV(v));

		p.addPeriod(new Period(120,SoundLoop.NONE, 0.2f, null).
				addVoice( new BinauralBeatVoice(70f, 10f, 0.65f)).
				addVoice( new BinauralBeatVoice(50f, 10f, 0.55f)).
				addVoice( new BinauralBeatVoice(3.7f, 3.7f, 0.4f)).
				addVoice( new BinauralBeatVoice(2.5f, 2.5f, 0.4f)).
				addVoice( new BinauralBeatVoice(5.9f, 5.9f, 0.4f)).
				setV(new Image(R.drawable.hiit_cooldown)));

		return p;
	}

	public static Program STIMULATION_HALLUCINATION(Program p) {
		p.setDescription(MyApp.getInstance().getString(R.string.program_hallucination_desc));
//		p.setDescription("A fun preset that simulates psychedelic hallucinations.");
		p.setAuthor("@GiorgioRegni");
		p.addPeriod(new Period(600,SoundLoop.WHITE_NOISE, 0.2f, null).
				addVoice( new BinauralBeatVoice(15f,	10f, 0.65f)).
				addVoice( new BinauralBeatVoice(15f,	10f, 0.65f)).
				addVoice( new BinauralBeatVoice(15f,	10f, 0.65f)).
				setV(new LSD()));
		return p;
	}

	public static Program SLEEP_SLEEP_INDUCTION( Program p) {
		Visualization v = new None();
		p.setDescription(MyApp.getInstance().getString(R.string.program_sleep_induction_desc));
		p.setDescription("Sleep induction for use about 15 mn before bedtime to help you fall asleep. It^s a one hour programm, with 6mn drop into delta waves followed by 54 mn of relaxing delta plateau");
		p.setAuthor("@thegreenman");
		p.addPeriod(new Period(360,SoundLoop.UNITY, 0.7f, null).
				addVoice( new BinauralBeatVoice(9.7f, 3.4f, 0.6f)).
				setV(v)
		).addPeriod(new Period(1620,SoundLoop.UNITY, 0.7f, null).
				addVoice( new BinauralBeatVoice(3.4f, 2.4f, 0.6f)).
				setV(v)
		).addPeriod(new Period(1620,SoundLoop.UNITY, 0.7f, null).
				addVoice( new BinauralBeatVoice(2.4f, 3.4f, 0.6f)).
				setV(v)
		);
		return p;
	}

	public static Program OOBE_LUCID_DREAMS( Program p) {

		Visualization m = new Black();
		p.setDescription(MyApp.getInstance().getString(R.string.program_lucid_dreams_desc));
//		p.setDescription("This preset stimulates lucid dreaming. It has to be played while sleeping, it is recommend during a nap, while seated in a chair or sofa to prevent falling fully asleep. 5hz base frequency with 8 hz spikes");
		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(60,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(10f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(60,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(60,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 6f, 0.6f)).
				setV(m)
		).addPeriod(new Period(60,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(6f, 6f, 0.6f)).
				setV(m)
		).addPeriod(new Period(120,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(6f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(360,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(180,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(90,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(240,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(480,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(10,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(360,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(m)
		).addPeriod(new Period(480,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(5f, 12f, 0.6f)).
				setV(m)
		);
		return p;
	}

	public static Program MEDITATION_SHAMANIC_RHYTHM(Program p) {
		Visualization v = new None();
		p.setDescription(MyApp.getInstance().getString(R.string.program_shamanic_rhythm_desc));
//		p.setDescription("Shamanic Drum Rhythm Begin at 9hz then glide down to 4.5hz for 50 minutes and back up at the end.");
		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice( new BinauralBeatVoice(9f, 4.5f, 0.6f)).
				setV(v)
		).addPeriod(new Period(3000,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice( new BinauralBeatVoice(4.5f, 4.5f, 0.6f)).
				setV(v)
		).addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice( new BinauralBeatVoice(4.5f, 9f, 0.6f)).
				setV(v)
		);

		return p;
	}

	public static Program SLEEP_SMR(Program p) {

		CanvasVisualization m = new Image(R.drawable.egg);
		p.setDescription(MyApp.getInstance().getString(R.string.program_smr_desc));
//		p.setDescription("Insomnia relief through stimulating Sensory Motor Rythm. Don\'t use it at bedtime, use it only during your insomnia, for example if you wake up in the middle of the night and can\'t go back to sleep 38 mn of crossing frequencies from 8 to 12 Hz.");
		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 8f, 0.6f)).
				addVoice( new BinauralBeatVoice(12f, 12f, 0.5f)).
				setV(m)
		).addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 10f, 0.5f)).
				addVoice( new BinauralBeatVoice(12f, 10f, 0.6f)).
				setV(m)
		).addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(10f, 12f, 0.6f)).
				addVoice( new BinauralBeatVoice(10f, 8f, 0.5f)).
				setV(m)
		).addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(12f, 12f, 0.5f)).
				addVoice( new BinauralBeatVoice(8f, 8f, 0.6f)).
				setV(m)
		).addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(12f, 10f, 0.6f)).
				addVoice( new BinauralBeatVoice(8f, 10f, 0.5f)).
				setV(m)
		).addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(10f, 8f, 0.5f)).
				addVoice( new BinauralBeatVoice(10f, 12f, 0.6f)).
				setV(m)
		).addPeriod(new Period(333,SoundLoop.NONE, 0.7f, null).
				addVoice( new BinauralBeatVoice(8f, 10f, 0.6f)).
				addVoice( new BinauralBeatVoice(12f, 10f, 0.5f)).
				setV(m)
		);

		return p;
	}

	public static Program OOBE_LUCID_DREAMS_2(Program p) {
		Visualization v = new Aurora();
		p.setDescription(MyApp.getInstance().getString(R.string.program_lucid_dreams_2_desc));
//		p.setDescription("Sleep Induction and Lucid Dreams 2 Sleep Induction into a longer lucid dreaming program");
		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(600,SoundLoop.UNITY, 0.7f, null).
				addVoice(new BinauralBeatVoice(9.7f, 3.4f, 0.6f)).
				addVoice(new BinauralBeatVoice(9.5f, 3f, 0.55f)).
				addVoice(new BinauralBeatVoice(9.3f, 2.4f, 0.5f)).
				setV(v)
		);

		p.addPeriod(new Period(5400,SoundLoop.UNITY, 0.7f, null).
				addVoice(new BinauralBeatVoice(3.4f, 2.4f, 0.6f)).
				addVoice( new BinauralBeatVoice(3f, 3f, 0.55f)).
				addVoice(new BinauralBeatVoice(2.4f, 3.4f, 0.5f)).
				setV(v)
		);

		p.addPeriod(new Period(120,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(10f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(120,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(8f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(120,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(8f, 6f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(240,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(6f, 6f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(240,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(6f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(420,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.UNITY, 0.7f, null).
				addVoice(new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(360,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.UNITY, 0.3f, null).
				addVoice(new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(180,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.NONE, 0.3f, null).
				addVoice(new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(900,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.UNITY, 0.3f, null).
				addVoice(new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(900,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 8f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(20,SoundLoop.UNITY, 0.3f, null).
				addVoice(new BinauralBeatVoice(8f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(360,SoundLoop.NONE, 0.7f, null).
				addVoice(new BinauralBeatVoice(5f, 5f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(360,SoundLoop.UNITY, 0.1f, null).
				addVoice(new BinauralBeatVoice(5f, 12f, 0.7f)).
				setV(v)
		);
		return p;
	}

	public static Program STIMULATION_ADHD(Program p) {
		Visualization v = new HypnoFlash();
		p.setDescription(MyApp.getInstance().getString(R.string.program_adhd_desc));
//		p.setDescription("This preset is an aid for Attention Deficit Hyperactivity Disorder. It lasts 30mn and alternates between 12 and 20 hz. It can be used whenever needed");
		p.setAuthor("@thegreenman");

		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice(new BinauralBeatVoice(60f, 12f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice(new BinauralBeatVoice(20f, 20f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice(new BinauralBeatVoice(12f, 12f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice(new BinauralBeatVoice(20f, 20f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice(new BinauralBeatVoice(12f, 12f, 0.6f)).
				setV(v)
		);
		p.addPeriod(new Period(300,SoundLoop.WHITE_NOISE, 0.7f, null).
				addVoice(new BinauralBeatVoice(20f, 20f, 0.6f)).
				setV(v)
		);

		return p;
	}
	public static Program SLEEP_POWERNAP(Program p) {
		Program p2 =fromGnauralFactory(readRawTextFile(R.raw.powernap));
		p2.name = p.name;
		return p2;
	}

	public static Program SLEEP_AIRPLANETRAVELAID(Program p) {
		Program p2 =fromGnauralFactory(readRawTextFile(R.raw.airplanetravelaid));
		p2.name = p.name;
		return p2;
	}


	public static String readRawTextFile(int resId) {
		InputStream inputStream = MyApp.getInstance().getResources().openRawResource(resId);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();

		try {
			while (( line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}

	private static boolean nameMatchCategory(String name, ProgramMeta.Category cat) {
		if (name.startsWith(cat.toString()+"_") == true)
			return true;
		else
			return false;
	}

	private static ProgramMeta.Category getMatchingCategory(String name) {
		for (ProgramMeta.Category i: ProgramMeta.Category.values()) {
			if (nameMatchCategory(name, i))
				return i;
		}

		throw new RuntimeException(String.format("Missing category in program %s", name));
	}
	public static Program fromGnauralFactory(String data) {
		Program p = null;
		Visualization v =  new None();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			db = dbf.newDocumentBuilder();

			Document doc = db.parse(new InputSource(new StringReader(data)));

			XPath xpath = XPathFactory.newInstance().newXPath();
			String title = (String) xpath.evaluate("/schedule/title/text()", doc, XPathConstants.STRING);
			String descr = (String) xpath.evaluate("/schedule/schedule_description/text()", doc, XPathConstants.STRING);
			String author = (String) xpath.evaluate("/schedule/author/text()", doc, XPathConstants.STRING);

			p = new Program(title);
			p.setDescription(descr);
			p.setAuthor(author);

			Period oldPeriod = null;

			NodeList NVoices = (NodeList) xpath.evaluate("/schedule/voice[1]/entries/entry", doc, XPathConstants.NODESET);
			for (int i = 0; i<NVoices.getLength(); i++) {
				Node n = NVoices.item(i);
				NamedNodeMap a = n.getAttributes();

				int len = new Float(a.getNamedItem("duration").getTextContent()).intValue();
				float vol = (new Float(a.getNamedItem("volume_left").getTextContent()) +
						new Float(a.getNamedItem("volume_right").getTextContent()))*1.5f;
				if (vol > 1f)
					vol = 1f;
				float beatfreq = new Float(a.getNamedItem("beatfreq").getTextContent());
				float basefreq = new Float(a.getNamedItem("basefreq").getTextContent());

				BinauralBeatVoice voice = new BinauralBeatVoice(beatfreq, beatfreq, vol, basefreq);
				Period period = new Period(len, SoundLoop.WHITE_NOISE, 0.1f, null).
						addVoice(voice).
						setV(v);

				if (oldPeriod != null)
					oldPeriod.getVoices().get(0).freqEnd = voice.freqStart;

				p.addPeriod(period);

				oldPeriod = period;
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return p;
	}
}
