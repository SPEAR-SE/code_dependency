package its.datastructure;

public class Method {
	private String packageName;
	private String className;
	private String methodName;
	private String signature;
	
	public Method(String packageName, String className, String methodName,String signature) {
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
		this.signature = signature;
	}
	public String getPackageName() {
		return this.packageName;
	}
	public String getClassName() {
		return this.className;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public String getSignature() {
		return this.signature;
	}
	public String toString() {
		return packageName + "." + className + "." + signature;
	}
	public int hashCode() {
		return (packageName + "." + className + "." + methodName + "." + signature).hashCode();
	}
	@Override
	public boolean equals(Object other) {
		Method m = (Method) other;
		if (this.getPackageName().equals(m.getPackageName())
				&& this.getClassName().equals(m.getClassName())
				&& this.getMethodName().equals(m.getMethodName())				
				&& this.getSignature().equals(m.getSignature()))//	
			return true;
		return false;
	}
	

}
