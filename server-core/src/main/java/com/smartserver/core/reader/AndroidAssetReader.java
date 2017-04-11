package com.smartserver.core.reader;

import android.content.res.AssetManager;
import android.text.TextUtils;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AndroidAssetReader implements AssetReader {

    private final AssetManager assetManager;

    public AndroidAssetReader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public InputStream getInputStream(String fileName) {
        try {
            return assetManager.open(fileName);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public boolean isFile(String fileName) {
        return getInputStream(fileName) != null;
    }

    public String[] fileList(String path) {
        try {
            return assetManager.list(path);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public List<String> scanFiles(String path) {
        List<String> paths = new ArrayList<>();
        if (isFile(path)) {
            paths.add(path);
        } else {
            String[] files = fileList(path);
            if (files != null && files.length > 0) {
                for (String file : files) {
                    String realPath = (TextUtils.isEmpty(path) ? "" : (path + File.separator)) + file;
                    if (isFile(realPath)) {
                        paths.add(realPath);
                    } else {
                        List<String> childList = scanFiles(realPath);
                        if (childList.size() > 0) {
                            paths.addAll(childList);
                        }
                    }
                }
            }
        }
        return paths;
    }
}
