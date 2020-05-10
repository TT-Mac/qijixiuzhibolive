package com.qiji.live.xiaozhibo.bean;

import java.util.List;

/**
 * Created by weipeng on 16/11/16.
 */

public class ResponseJson<T>{

    /**
     * ret : 200
     * data : {"code":1001,"msg":"手机号码不一致","info":[]}
     * msg :
     */

    private int ret;
    /**
     * code : 1001
     * msg : 手机号码不一致
     * info : []
     */

    private DataBean<T> data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean<T> {
        private int code;
        private String msg;
        private List<T> info;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<T> getInfo() {
            return info;
        }

        public void setInfo(List<T> info) {
            this.info = info;
        }
    }
}
