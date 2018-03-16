package com.coder.binauralbeats.beats;
import java.io.Serializable;
import java.lang.reflect.Method;

public class ProgramMeta implements Serializable {

	public enum Category implements Serializable{
		HYPNOSIS,
		SLEEP,
		HEALING,
		LEARNING,
		MEDITATION,
		STIMULATION,
		OOBE,
		OTHER
	}

	private Method method;
	private String   name;
	private Category cat;
	private CategoryGroup group;
	private Program program;

	public ProgramMeta(Method method, String name, Category cat) {
		this.method = method;
		this.name = name;
		this.cat = cat;
		this.group = null;
	}

	public Method getMethod() {
		return method;
	}

	public String getName() {
		return name;
	}

	public Category getCat() {
		return cat;
	}

	public CategoryGroup getGroup() {
		return group;
	}

	public void setGroup(CategoryGroup group) {
		this.group = group;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}
}
