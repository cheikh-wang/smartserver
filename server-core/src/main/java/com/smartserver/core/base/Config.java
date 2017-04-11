package com.smartserver.core.base;

import com.smartserver.core.reader.AssetReader;

/**
 * author: cheikh.wang on 17/4/11
 * email: wanghonghi@126.com
 */
public class Config {
    private final int port;
    private final AssetReader assetReader;

    private Config(Builder builder) {
        this.port = builder.port;
        this.assetReader = builder.assetReader;
    }

    public static class Builder {
        private int port;
        private AssetReader assetReader;
        private String defaultIndex;

        public Builder() {}

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder assetReader(AssetReader assetReader) {
            this.assetReader = assetReader;
            return this;
        }

        public Config build() {
            if (defaultIndex == null) {
                defaultIndex = "index.html";
            }
            if (assetReader == null) {
                throw new IllegalStateException("assetReader can not be null");
            }
            if (port < 1024 || port > 65535) {
                throw new IllegalStateException("the valid port number range is 1024 ~ 65535");
            }

            return new Config(this);
        }
    }

    public int getPort() {
        return port;
    }

    public AssetReader getAssetReader() {
        return assetReader;
    }
}
