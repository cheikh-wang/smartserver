package com.smartserver.core.url;

import com.smartserver.core.Application;
import com.smartserver.core.exception.InvalidRouteException;
import com.smartserver.core.http.HttpRequest;
import com.smartserver.core.reader.AssetReader;
import com.smartserver.generated.RuleRegistry;
import java.util.ArrayList;
import java.util.List;

public class UrlManager {

    private List<Rule> defaultRules;
    private List<Rule> logicRules;

    private static UrlManager sInstance;

    public static UrlManager getInstance() {
        if (sInstance == null) {
            sInstance = new UrlManager();
        }
        return sInstance;
    }

    private UrlManager() {
        defaultRules = new ArrayList<>();
        logicRules = new ArrayList<>();
        initDefaultRules();
    }

    private void initDefaultRules() {
        // apt scan action
        List<String> rules = new RuleRegistry().getRules();
        if (rules != null) {
            for (String rule : rules) {
                addDefaultRule(rule);
            }
        }
        // resource files
        AssetReader assetReader = Application.getInstance().getConfig().getAssetReader();
        List<String> paths = assetReader.scanFiles("");
        if (paths != null) {
            for (String path : paths) {
                addDefaultRule(Rule.createRule("GET", path));
            }
        }
    }

    public void setLogicRules(List<String> rules) {
        logicRules.clear();
        if (rules != null) {
            Rule compiledRule;
            for (String rule : rules) {
                compiledRule = buildRule(rule);
                logicRules.add(compiledRule);
            }
        }
    }

    private void addDefaultRule(String rule) {
        Rule compiledRule = buildRule(rule);
        defaultRules.add(compiledRule);
    }

    private Rule buildRule(String rule) {
        Rule compiledRule;
        String[] arr = rule.split(" ");
        if (arr.length >= 4) {
            compiledRule = new Rule(arr[0], arr[1], false, rule);
        } else if (arr.length == 2) {
            compiledRule = new Rule(arr[0], arr[1], true, rule);
        } else {
            throw new InvalidRouteException("invalid route rule: " + rule);
        }

        return compiledRule;
    }

    public Route parseRequest(HttpRequest request) {
        for (Rule rule : defaultRules) {
            Route route = rule.parseRequest(this, request);
            if (route != null) {
                return route;
            }
        }
        for (Rule rule : logicRules) {
            Route route = rule.parseRequest(this, request);
            if (route != null) {
                return route;
            }
        }
        return null;
    }
}
