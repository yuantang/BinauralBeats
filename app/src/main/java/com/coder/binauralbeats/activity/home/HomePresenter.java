package com.coder.binauralbeats.activity.home;

import android.util.Log;

import com.coder.binauralbeats.MyApp;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.beats.CategoryGroup;
import com.coder.binauralbeats.beats.DefaultProgramsBuilder;
import com.coder.binauralbeats.beats.ProgramMeta;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author TUS
 * @date 2018/3/20
 */

public class HomePresenter extends MvpBasePresenter<HomeView> {
    Map<String,ProgramMeta> programs;
    ArrayList<CategoryGroup> groups;
    public void loadData(){
        programs = DefaultProgramsBuilder.getProgramMethods(MyApp.getInstance());
        groups = new ArrayList<>();
        for (String pname: programs.keySet()) {
            ProgramMeta pm = programs.get(pname);
            String catname = pm.getCat().toString();
            CategoryGroup g = null;
            for (CategoryGroup g2: groups) {
                if (g2.getName().equals(catname)) {
                    g = g2;
                }
            }
            if (g == null) {
                g = new CategoryGroup(catname);
                try {
                    Log.e("TAG", catname.toLowerCase());
                    g.setNiceName(MyApp.getInstance().getString(R.string.class.getField("group_"+catname.toLowerCase()).getInt(null)));
                } catch (Exception e) {

                }
                groups.add(g);
            }
            pm.setProgram(DefaultProgramsBuilder.getProgram(pm));
            g.add(pm);
        }


        getView().showData(groups);
    }
//    public void scan() {
//
//        load(ProgramMeta.Category.HYPNOSIS, R.string.group_hypnosis);
//        load(ProgramMeta.Category.SLEEP, R.string.group_sleep);
//        load(ProgramMeta.Category.HEALING, R.string.group_healing);
//        load(ProgramMeta.Category.LEARNING, R.string.group_learning);
//        load(ProgramMeta.Category.MEDITATION, R.string.group_mediation);
//        load(ProgramMeta.Category.STIMULATION, R.string.group_simulation);
//        load(ProgramMeta.Category.OOBE, R.string.group_oobe);
//        load(ProgramMeta.Category.OTHER);
//
//        load(R.string.program_morphine, "HEALING_MORPHINE");
//        load(R.string.program_learning, "LEARNING_LEARNING");
//        load(R.string.program_wakefulrelax, "MEDITATION_WAKEFULRELAX");
//        load(R.string.program_schumann_resonance, "MEDITATION_SCHUMANN_RESONANCE");
//        load(R.string.program_unity, "MEDITATION_UNITY");
//        load(R.string.program_shamanic_rhythm, "MEDITATION_SHAMANIC_RHYTHM");
//        load(R.string.program_powernap, "SLEEP_POWERNAP");
//        load(R.string.program_sleep_induction, "SLEEP_SLEEP_INDUCTION");
//        load(R.string.program_smr, "SLEEP_SMR");
//        load(R.string.program_airplanetravelaid, "SLEEP_AIRPLANETRAVELAID");
//        load(R.string.program_adhd, "STIMULATION_ADHD");
//        load(R.string.program_hiit, "STIMULATION_HIIT");
//        load(R.string.program_hallucination, "STIMULATION_HALLUCINATION");
//        load(R.string.program_highest_mental_activity, "STIMULATION_HIGHEST_MENTAL_ACTIVITY");
//        load(R.string.program_creativity, "STIMULATION_CREATIVITY");
//        load(R.string.program_self_hypnosis, "HYPNOSIS_SELF_HYPNOSIS");
//        load(R.string.program_lucid_dreams, "OOBE_LUCID_DREAMS");
//        load(R.string.program_astral_01_relax, "OOBE_ASTRAL_01_RELAX");
//        load(R.string.program_lucid_dreams_2, "OOBE_LUCID_DREAMS_2");
//
//
//    }
//
//    void load(ProgramMeta.Category c) {
//        cats.put(c, c.toString().toUpperCase());
//    }
//
//    void load(ProgramMeta.Category c, int id) {
//        String n = getString(id);
//        cats.put(c, n.toUpperCase());
//    }
//
//    void load(int id, String name) {
//        String desc = getString(id);
//        String[] ss = desc.split("\\|");
//        Program p = new Program(ss[0]);
//        Method m;
//        try {
//            m = DefaultProgramsBuilder.class.getDeclaredMethod(name, Program.class);
//            p = (Program) m.invoke(null, p);
//        } catch (NoSuchMethodException e) {
//            try {
//                m = DefaultProgramsBuilder.class.getDeclaredMethod(name, Context.class, Program.class);
//                p = (Program) m.invoke(null, MainActivity.this, p);
//            } catch (Exception ee) {
//                Log.d(TAG, "Load error", ee);
//                return;
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "Load error", e);
//            return;
//        }
//        if (ss.length > 1)
//            p.setDescription(ss[1]);
//        ProgramMeta.Category c = DefaultProgramsBuilder.getMatchingCategory(name);
//        ProgramMeta pm = new ProgramMeta(m, p.getName(), c);
//        library.put(pm, p);
//        if (old != c) {
//            old = c;
//            add(cats.get(c));
//        }
//        add(pm);
//    }
}
