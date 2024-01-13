
var Level = {
    "trace": 1 << 0,
    "debug": 1 << 1,
    "info": 1 << 2,
    "warn": 1 << 3,
    "error": 1 << 4,
    "none": 1 << 5
}

let Log = {
    getLevel: function() {
        return Level[("" || "none").toLowerCase()] || Level["none"];
    },
    isDebugEnabled: function () {
        return Level["debug"] >= this.getLevel();
    },
    isInfoEnabled: function () {
        return Level["info"] >= this.getLevel();
    },
    isWarnEnabled: function () {
        return Level["warn"] >= this.getLevel();
    },
    isErrorEnabled: function () {
        return Level["error"] >= this.getLevel();
    },
    debug: function (...args) {
        if (this.isDebugEnabled())
            console && console.debug && console.debug(args)
    },
    info: function (...args) {
        if (this.isInfoEnabled())
            console && console.info && console.info(args)
    },
    warn: function (...args) {
        if (this.isWarnEnabled())
            console && console.warn && console.warn(args)
    },
    error: function (...args) {
        if (this.isErrorEnabled())
            console && console.debug && console.error(args)
    },
}

const MRest = function (layui) {
    this.layui = layui;
    this.layer = this.layui.layer;
};
MRest.prototype = {
    /**
     * axios请求处理
     * @param options 请求处理参数及回调
     * {
     *     method:  "",     // [必输]请求方法: post,get,put,delete,...
     *     url0:    "",     // [非必输]实际请求处理url（通过url处理请求，隐藏真实请求url）
     *     url:     "",     // [必输]请求url
     *     params:  {},     // [非必输]url参数
     *     data:    {},     // [非必输]请求body
     *     headers: {},     // [非必输]请求头
     *     callback:    (data,resp) => {}           // [非必输]请求成功回调，data为接口返回结果
     *     succFunc:    (data,resp) => {}           // [非必输]请求成功&交易成功回调，data为接口返回结果data属性
     *     failFunc:    (data,resp) => {}           // [非必输]请求成功&交易失败回调，data为接口返回结果
     *     errorFunc:   (options,resp) => {}        // [非必输]请求失败回调
     *     excpFunc:    (options,error) => {}       // [非必输]请求处理异常回调
     * }
     */
    axiosRequest: function (options) {
        var loadIndex = this.layer.load(0);
        let startTimeMillis = new Date().getTime()
        let params = options.params || {}
        let data = options.data || {}
        let headers = options.headers || {}
        axios({
            method: options.method,
            url: options.url,
            params: params,
            data: data,
            headers: headers
        }).then(resp => {
            this.layer.close(loadIndex);
            let costTimeMillis = new Date().getTime() - startTimeMillis
            if (Log.isDebugEnabled()) {
                Log.debug(`[${options.url0 || options.url}]请求[参数|header|data|响应]`, params, headers, data, resp, `耗时: ${costTimeMillis}ms`)
            }
            try {
                /**
                 * callback -> succFunc -> failFunc -> errorFunc -> excpFunc
                 */
                if (resp.status && resp.status === 200) {
                    if (typeof options.callback === "function") {
                        return options.callback(resp.data, resp)
                    }
                    let data = resp.data || {}
                    if (data.success) {
                        (typeof options.succFunc === "function" ? options.succFunc : this.defaultSuccFunc())(data.data, resp)
                    } else {
                        (typeof options.failFunc === "function" ? options.failFunc : this.defaultFailFunc())(data, resp)
                    }
                } else {
                    (typeof options.errorFunc === "function" ? options.errorFunc : this.defaultErrorFunc())(options, resp)
                }
            } catch (error) {
                let costTimeMillis = new Date().getTime() - startTimeMillis
                if (Log.isErrorEnabled()) {
                    Log.debug(`url0=[${options.url0}],url=[${options.url}]请求[参数|header|data|异常]`, params, headers, data, error, `耗时: ${costTimeMillis}ms`)
                }
                (typeof options.excpFunc === "function" ? options.excpFunc : this.defaultExceptionFunc())(options, error)
            }
        }).catch(error => {
            this.layer.close(loadIndex);
            let costTimeMillis = new Date().getTime() - startTimeMillis
            if (Log.isDebugEnabled()) {
                Log.debug(`url0=[${options.url0}],url=[${options.url}]请求[参数|header|data|异常]`, params, headers, data, error, `耗时: ${costTimeMillis}ms`)
            }
            (typeof options.excpFunc === "function" ? options.excpFunc : this.defaultExceptionFunc())(options, error)
        })
    },
    /**
     * 默认请求成功-处理成功 回调
     * @returns {function(...[*]=)}
     */
    defaultSuccFunc: function () {
        return (data, resp) => {
            /*do nothing.*/
        }
    },
    /**
     * 默认请求成功-处理失败 回调
     * @returns {function(...[*]=)}
     */
    defaultFailFunc: function () {
        return (data, resp) => {
            var message = `code=[${data.code}], message=[${data.message}]`;
            this.layer.alert(message, {}, (index) => {
                this.layer.close(index);
            });
        }
    },
    /**
     * 默认请求失败（响应码非200）回调
     * @returns {function(...[*]=)}
     */
    defaultErrorFunc: function () {
        return (options, resp) => {
            var message = `request url0=[${options.url0}],url=[${options.url}] failed, status=[${resp.status}], statusText=[${resp.statusText}]`;
            if (resp.data) {
                message += ", resp.data=" + JSON.stringify(resp.data);
            }
            this.layer.alert(message, {}, (index) => {
                this.layer.close(index);
            });
        }
    },
    /**
     * 默认请求处理异常回调
     * @returns {function(...[*]=)}
     */
    defaultExceptionFunc: function () {
        return (options, error) => {
            let resp = error.response || {}
            if (error instanceof Error) {
                resp = {
                    status: -1,
                    statusText: `${error.name},${error.message},${error.stack}`,
                    data: ''
                }
            }
            let data = resp.data
            let errMsg = `request url0=[${options.url0}],url=[${options.url}] failed, status=[${resp.status || -1}], statusText=[${resp.statusText || ''}]`
            if (data && data.success !== undefined && data.data !== undefined && data.code !== undefined && data.message !== undefined) {
                errMsg = `${errMsg}, code=${data.code}, message=${data.message}`
            }
            this.layer.alert(errMsg, {}, (index) => {
                this.layer.close(index);
            });
        }
    }
}
