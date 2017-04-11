package com.smartserver.core.reader;

import java.io.InputStream;
import java.util.List;

public interface AssetReader {

    InputStream getInputStream(String fileName);

    List<String> scanFiles(String path);
}
