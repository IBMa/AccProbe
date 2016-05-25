/* DO NOT EDIT THIS FILE - it is machine generated */
#include "org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink.h"


IAccessibleHyperlink* GetRef( JNIEnv *env, jobject jca){
    jclass cls = env->GetObjectClass(jca);
    jmethodID methID = env->GetMethodID(cls, "internalRef", "()I");
    return (IAccessibleHyperlink*) env->CallIntMethod(jca, methID);
}


/*
 * Class:     org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink
 * Method:    internalGetAccessibleAnchor
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jobject JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink_internalGetAnchor
  (JNIEnv * env, jobject jca, jint x) { 
	  IAccessibleHyperlink* ptr = GetRef(env,jca);
	  VARIANT anchor;
	  HRESULT hr = ptr->get_anchor(x, &anchor);
	  if(hr!=S_OK){
			putErrorCode(_T("accessibleAnchor"), CAccessible::getHRESULTString(hr), env, jca);
	  }
	  if(SUCCEEDED(hr)){
		return getString(anchor,env);
	  }
	  return 0;
}

/*
 * Class:     org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink
 * Method:    internalGetAccessibleAnchorTarget
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jobject JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink_internalGetAnchorTarget
  (JNIEnv * env, jobject jca, jint x) { 
	  IAccessibleHyperlink* ptr = GetRef(env,jca);
	  VARIANT anchorTarget;
	  HRESULT hr = ptr->get_anchorTarget(x, &anchorTarget);
	  if(hr!=S_OK){
			putErrorCode(_T("accessibleAnchorTarget"), CAccessible::getHRESULTString(hr), env, jca);
	  }
	  if(SUCCEEDED(hr)){
		return getString(anchorTarget, env);
	  }
	  return 0;
}

/*
 * Class:     org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink
 * Method:    internalGetStartIndex
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink_internalGetStartIndex
  (JNIEnv * env, jobject jca) { 
	IAccessibleHyperlink* ptr = GetRef(env,jca);
	long stIndex=0;
	HRESULT hr = ptr->get_startIndex(&stIndex);
	  if(hr!=S_OK){
			putErrorCode(_T("startIndex"), CAccessible::getHRESULTString(hr), env, jca);
	  }
	return (int)stIndex;
}

/*
 * Class:     org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink
 * Method:    internalGetEndIndex
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink_internalGetEndIndex
  (JNIEnv * env, jobject jca) { 
	IAccessibleHyperlink* ptr = GetRef(env,jca);
	long endIndex=0;
	HRESULT hr = ptr->get_endIndex(&endIndex);
	  if(hr!=S_OK){
			putErrorCode(_T("endIndex"), CAccessible::getHRESULTString(hr), env, jca);
	  }
	return (int)endIndex;
}

/*
 * Class:     org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink
 * Method:    internalIsValid
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_a11y_utils_accprobe_accservice_core_win32_ia2_IA2AccessibleHyperlink_internalIsValid
  (JNIEnv * env, jobject jca) {
	IAccessibleHyperlink* ptr = GetRef(env,jca);
	boolean valid =  false;
	HRESULT hr = ptr->get_valid(&valid);
	  if(hr!=S_OK){
			putErrorCode(_T("isValid"), CAccessible::getHRESULTString(hr), env, jca);
	  }
	return valid;
}