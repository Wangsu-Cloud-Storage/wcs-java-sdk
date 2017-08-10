package com.chinanetcenter.api.entity;

/**
 * 上传策略对象<br>
 * <!--
 * <table border="1px solid">
 * 	<tr>
 * 		<th>字段名</th>
 * 		<th width="60px">是否必填</th>
 * 		<th>描述</th>
 * 	</tr>
 * 	<tr>
 * 		<td>scope</td>
 * 		<td>是</td>
 * 		<td>指定上传的目标资源空间（Bucket）和资源名（Key）有两种格式：1. <bucket>，表示允许用户上传文件到指定的 bucket。2. <bucket>:<filename>，表示允许用户上传指定filename的文件。</td>
 * 	</tr>
 * 	<tr>
 * 		<td>deadline</td>
 * 		<td>是</td>
 * 		<td>上传请求授权的截止时间；UNIX时间戳，单位：豪秒。范例：1398916800000，代表时间2014-05-01 12:00:00。</td>
 * 	</tr>
 * 	<tr>
 * 		<td>returnUrl</td>
 * 		<td>否</td>
 * 		<td>Web端文件上传成功或失败后，浏览器都会执行303跳转的URL；通常用于HTML Form上传。(1)文件上传成功后会跳转到<returnUrl>?upload_ret=<queryString>, <queryString>包含returnBody内容。(2)文件上传失败后会跳转到<returnUrl>?code=<code>&message=<message>, <code>是错误码，<message>是错误具体信息。如不设置returnUrl，则直接将returnBody的内容返回给客户端。</td>
 * 	</tr>
 * 	<tr>
 * 		<td>returnBody</td>
 * 		<td>否</td>
 * 		<td>
 * 			上传成功后，自定义最终返回給上传端（该字段配合returnUrl使用）的数据。如果您只需要返回文件大小和文件地址，只需将returnBody设置成fname=$(fname)&fsize=$(fsize)&url=$(url)即可。
 * 			<ul>
 * 				<li>
 * 					自定义替换变量，格式如下：$(x:variable)，范例：position=$(x:position)&message=$(x:message)
 * 				</li>
 * 				<li>
 * 					特殊替换变量
 * 					<table border="1px solid">
 * 						<tr>
 * 							<th>参数值</th>
 * 							<th>描述</th>
 * 						</tr>
 * 						<tr>
 * 							<td>$(bucket)</td>
 * 							<td>获得上传的目标空间名</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(key)</td>
 * 							<td>获得文件保存在空间中的资源名</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fname)</td>
 * 							<td>上传的原始文件名</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(hash)</td>
 * 							<td>资源唯一标识（hash(bucket:fname)）</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fsize)</td>
 * 							<td>资源尺寸，单位为字节</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(url)</td>
 * 							<td>访问该资源的实际路径，经过URL安全的Base64编码，使用时需要相应解析下</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(costTime)</td>
 * 							<td>此次请求消耗的时间</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(ip)</td>
 * 							<td>此次请求的来源IP</td>
 * 						</tr>
 * 					</table>
 * 				</li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>fsizeLimit</td>
 * 		<td>否</td>
 * 		<td>限定上传文件的大小，单位：字节（Byte）；超过限制的上传内容会被判为上传失败，返回413状态码。</td>
 * 	</tr>
 * 	<tr>
 * 		<td>overwrite</td>
 * 		<td>否</td>
 * 		<td>指定是否覆盖服务器上已经存在的文件；0:不覆盖；1：覆盖</td>
 * 	</tr>
 * 	<tr>
 * 		<td>callbackUrl</td>
 * 		<td>否</td>
 * 		<td>上传成功后，网宿云以POST方式请求该callbackUrl（必须公网URL地址，能正常响应HTTP/1.1 200 OK）。要求 callbackUrl 的Response返回数据格式为JSON文本体 ，即Content-Type 为 "application/json"。</td>
 * 	</tr>
 * 	<tr>
 * 		<td>callbackBody</td>
 * 		<td>否</td>
 * 		<td>
 * 			上传成功后，网宿云POST方式提交请求的数据。callbackBody 要求是合法的 url query string。如：key=$(key) &fsize=$(fsize)
 *			<ul>
 * 				<li>
 * 					自定义替换变量，格式如下：$(x:variable)，范例：position=$(x:position)&message=$(x:message)
 * 				</li>
 * 				<li>
 * 					特殊替换变量
 * 					<table border="1px solid">
 * 						<tr>
 * 							<th>参数值</th>
 * 							<th>描述</th>
 * 						</tr>
 * 						<tr>
 * 							<td>$(bucket)</td>
 * 							<td>获得上传的目标空间名</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(key)</td>
 * 							<td>获得文件保存在空间中的资源名</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fname)</td>
 * 							<td>上传的原始文件名</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(hash)</td>
 * 							<td>资源唯一标识（hash(bucket:fname)）</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fsize)</td>
 * 							<td>资源尺寸，单位为字节</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(url)</td>
 * 							<td>访问该资源的实际路径，经过URL安全的Base64编码，使用时需要相应解析下</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(costTime)</td>
 * 							<td>此次请求消耗的时间</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(ip)</td>
 * 							<td>此次请求的来源IP</td>
 * 						</tr>
 * 					</table>
 * 				</li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>persistentOps</td>
 * 		<td>否</td>
 * 		<td>
 * 			上传成功后，触发待执行的处理指令列表。每个指令是一个规范的字符串，多个指令用“;”分隔。
 * 			<table border="1px solid">
 * 				<tr>
 * 					<th>指令</th>
 * 					<th>说明</th>
 * 				</tr>
 * 				<tr>
 * 					<td>avthumb/Format</td> 
 * 					<td>Format（必填）-目标视频的格式（支持flv），范例：avthumb/flv</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/<Format></td> 
 * 					<td>Format（必填）-目标图的格式，jpg等。范例：vframe/jpg</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/offset/<Second></td> 
 * 					<td>offset/<Second>（必填）-指定视频帧的时刻，单位s，范例:vframe/offset/7</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/w/<Width></td> 
 * 					<td>w/<Width>（可选）-指定截取图片的宽度，单位px（如果不指定该值，会使用视频默认宽度）</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/h/<Height></td> 
 * 					<td>h/<Height>（可选）-指定截取图片的高度，单位px（如果不指定该值，会使用视频默认高度）</td>
 * 				</tr>
 * 			</table>
 *		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>persistentNotifyUrl</td>
 * 		<td>否</td>
 * 		<td>接收预处理结果通知的URL（必须公网URL地址，能正常响应HTTP/1.1 200 OK）。提示：请在设置persistenOps字段时，完成persistentNotifyUrl字段设置，平台会通过调用persistentNotifyUrl字段设置的URL，来通知您指令处理的结果。</td>
 * 	</tr>
 * </table>
 * -->
 * @author zouhao
 * @version 1.0
 * @since 2014/02/14
 */
public class PutPolicy {

    /**
     * 指定上传的目标资源空间（bucektName）和资源名（fileName）
     * 格式为bucektName:fileName
     */
    private String scope;
    /**
     * 有效时间, 时间的Long类型, 单位为毫秒
     */
    private String deadline;
    /**
     * 上传成功后，自定义最终返回給上传端（该字段配合returnUrl使用）的数据。<br />
     * 返回的内容<br />
     * 格式例子: $(bucket)&$(fsize)&$(hash)&$(key)<br />
     */
    private String returnBody;
    /**
     * 指定是否覆盖服务器上已经存在的文件<br />
     * 1-允许覆盖, 0-不允许
     */
    private int overwrite;
    /**
     * 限定上传文件的大小
     */
    private long fsizeLimit;
    /**
     * Web端文件上传成功后，浏览器执行303跳转的URL
     */
    private String returnUrl;
    /**
     * 回调url
     */
    private String callbackUrl;
    /**
     * 回调内容<br />
     * 格式例子:<keyName>=(keyValue)&<keyName>=(keyValue)<br />
     * 必须以键值的格式
     */
    private String callbackBody;
    /**
     * 持久化操作指令列表<br />
     * 转换为flv指令：avthumb/flv/vb/1.25m<br />
     * 视频截图指令：vframe/jpg/offset/1<br />
     * 使用分号";"分隔
     */
    private String persistentOps;
    /**
     * 持久化操作通知Url
     */
    private String persistentNotifyUrl;

    private String lastModifiedTime;
    private Integer instant;
    private String saveKey;
    private Long separate;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getReturnBody() {
        return returnBody;
    }

    public void setReturnBody(String returnBody) {
        this.returnBody = returnBody;
    }

    public int getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(int overwrite) {
        this.overwrite = overwrite;
    }

    public long getFsizeLimit() {
        return fsizeLimit;
    }

    public void setFsizeLimit(long fsizeLimit) {
        this.fsizeLimit = fsizeLimit;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackBody() {
        return callbackBody;
    }

    public void setCallbackBody(String callbackBody) {
        this.callbackBody = callbackBody;
    }

    public String getPersistentOps() {
        return persistentOps;
    }

    public void setPersistentOps(String persistentOps) {
        this.persistentOps = persistentOps;
    }

    public String getPersistentNotifyUrl() {
        return persistentNotifyUrl;
    }

    public void setPersistentNotifyUrl(String persistentNotifyUrl) {
        this.persistentNotifyUrl = persistentNotifyUrl;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Integer getInstant() {
        return instant;
    }

    public void setInstant(Integer instant) {
        this.instant = instant;
    }

    public String getSaveKey() {
        return saveKey;
    }

    public void setSaveKey(String saveKey) {
        this.saveKey = saveKey;
    }

    public Long getSeparate() {
        return separate;
    }

    public void setSeparate(Long separate) {
        this.separate = separate;
    }

    @Override
    public String toString() {
        return "PutPolicy{" +
                "scope='" + scope + '\'' +
                ", deadline='" + deadline + '\'' +
                ", returnBody='" + returnBody + '\'' +
                ", overwrite=" + overwrite +
                ", fsizeLimit=" + fsizeLimit +
                ", returnUrl='" + returnUrl + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", callbackBody='" + callbackBody + '\'' +
                ", persistentOps='" + persistentOps + '\'' +
                ", persistentNotifyUrl='" + persistentNotifyUrl + '\'' +
                ", instant='" + instant + '\'' +
                ", saveKey='" + saveKey + '\'' +
                ", separate='" + separate + '\'' +
                '}';
    }

}
