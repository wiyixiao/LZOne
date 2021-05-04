package com.wiyixiao.lzone.interfaces;

public interface IZipStateCallback {
    /**
     * 压缩/解压成功
     */
    void onFailed();

    /**
     * 压缩/解压失败
     */
    void onSuccess();
}
