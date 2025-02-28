/**
 * 创建时间
 * 2015年3月14日-下午6:43:58
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午6:43:58
 * 
 * @version 1.0.0
 * 
 */
public class AppCrmUserRequest extends RequestBase {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -1762554830989958126L;
	private String userid;
	private String pwd;

	private String mac;
	private String imei;

	/**
	 * pwd
	 *
	 * @return the pwd
	 * @since 1.0.0
	 */

	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * mac
	 *
	 * @return the mac
	 * @since 1.0.0
	 */

	public String getMac() {
		return mac;
	}

	/**
	 * @param mac
	 *            the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * imei
	 *
	 * @return the imei
	 * @since 1.0.0
	 */

	public String getImei() {
		return imei;
	}

	/**
	 * @param imei
	 *            the imei to set
	 */
	public void setImei(String imei) {
		this.imei = imei;
	}

	/**
	 * userid
	 *
	 * @return the userid
	 * @since 1.0.0
	 */

	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

}
