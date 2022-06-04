package creation;

import java.util.HashMap;
import java.util.Map;

/**
 * 工厂模式分为三种：简单工厂、工厂方法、抽象工厂(不常用)
 * 如果创建逻辑比较复杂, 可以考虑使用工厂模式, 封装对象的创建过程, 将对象的创建和使用相分离
 */

class RuleConfig {}

interface IRuleConfigParser {
    RuleConfig parse(String configText);
}

class JsonRuleConfigParser implements IRuleConfigParser {

    @Override
    public RuleConfig parse(String configText) {
        return new RuleConfig();
    }
}

class XmlRuleConfigParser implements IRuleConfigParser {

    @Override
    public RuleConfig parse(String configText) {
        return new RuleConfig();
    }
}

class YamlRuleConfigParser implements IRuleConfigParser {

    @Override
    public RuleConfig parse(String configText) {
        return new RuleConfig();
    }
}

class PropertiesRuleConfigParser implements IRuleConfigParser {

    @Override
    public RuleConfig parse(String configText) {
        return new RuleConfig();
    }
}


/**
 * example: 根据配置文件的后缀选择不同的解析器
 */
class RuleConfigSource1 {

    public RuleConfig load(String ruleConfigFilePath) throws Exception {

        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser = null;
        if("json".equalsIgnoreCase(ruleConfigFileExtension)){
            parser = new JsonRuleConfigParser();
        }
        else if("xml".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new XmlRuleConfigParser();
        }
        else if("yaml".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new YamlRuleConfigParser();
        }
        else if("properties".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new PropertiesRuleConfigParser();
        }
        else {
            throw new Exception("error config file format");
        }

        return parser.parse("...");
    }

    private String getFileExtension(String filePath) {
        // ....
        return "json";
    }
}


/**
 * 为了代码逻辑更清晰可以将parser创建的部分逻辑剥离出来, 重构后代码如下所示：
 */
class RuleConfigSource2 {

    public RuleConfig load(String ruleConfigFilePath) throws Exception {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser = createParser(ruleConfigFileExtension);
        if(parser == null)
            throw new Exception("error config file format");

        return parser.parse("...");
    }


    private String getFileExtension(String filePath) {
        // ....
        return "json";
    }


    private IRuleConfigParser createParser(String ruleConfigFileExtension) {

        IRuleConfigParser parser = null;

        if("json".equalsIgnoreCase(ruleConfigFileExtension)){
            parser = new JsonRuleConfigParser();
        }
        else if("xml".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new XmlRuleConfigParser();
        }
        else if("yaml".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new YamlRuleConfigParser();
        }
        else if("properties".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new PropertiesRuleConfigParser();
        }

        return parser;
    }
}


/**
 * 为了让类的职责更单一、代码更清晰, 可以进一步将createParser函数剥离到一个独立的类中, 这就是简单工厂模式
 */
class RuleConfigSource3 {
    public RuleConfig load(String ruleConfigFilePath) throws Exception {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser = RuleConfigParserFactory.createParser(ruleConfigFileExtension);
        if(parser == null)
            throw new Exception("error config file format");

        return parser.parse("...");
    }


    private String getFileExtension(String filePath) {
        // ....
        return "json";
    }
}

class RuleConfigParserFactory {
    private static final Map<String,IRuleConfigParser> cachedParsers = new HashMap<>();

    static {
        cachedParsers.put("json", new JsonRuleConfigParser());
        cachedParsers.put("xml", new XmlRuleConfigParser());
        cachedParsers.put("yaml", new YamlRuleConfigParser());
        cachedParsers.put("properties", new PropertiesRuleConfigParser());
    }

    public static IRuleConfigParser createParser(String configFormat) {
        if(configFormat == null || configFormat.isEmpty()) {
            return null;
        }
        return cachedParsers.get(configFormat.toLowerCase());
    }
}


/**
 * 工厂方法 利用多态, 重构后代码如下：
 */
interface IRuleConfigParserFactory {
   IRuleConfigParser createParser();
}

class JsonRuleConfigParserFactory implements IRuleConfigParserFactory {

    @Override
    public IRuleConfigParser createParser() {
        return new JsonRuleConfigParser();
    }
}

class XmlRuleConfigParserFactory implements IRuleConfigParserFactory {

    @Override
    public IRuleConfigParser createParser() {
        return new XmlRuleConfigParser();
    }
}

class YamlRuleConfigParserFactory implements IRuleConfigParserFactory {

    @Override
    public IRuleConfigParser createParser() {
        return new YamlRuleConfigParser();
    }
}

class PropertiesRuleConfigParserFactory implements IRuleConfigParserFactory {

    @Override
    public IRuleConfigParser createParser() {
        return new PropertiesRuleConfigParser();
    }
}

class RuleConfigSource4 {
    public RuleConfig load(String ruleConfigFilePath) throws Exception {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);

        IRuleConfigParserFactory parserFactory = RuleConfigParserFactoryMap.getParserFactory(ruleConfigFileExtension);
        if(parserFactory == null) {
            throw new Exception("error config file format");
        }
        return parserFactory.createParser().parse("...");
    }


    private String getFileExtension(String filePath) {
        // ....
        return "json";
    }
}

/**
 * 工厂的工厂
 */
class RuleConfigParserFactoryMap {
    private static final Map<String, IRuleConfigParserFactory> cachedFactories = new HashMap<>();

    static {
        cachedFactories.put("json", new JsonRuleConfigParserFactory());
        cachedFactories.put("xml", new XmlRuleConfigParserFactory());
        cachedFactories.put("yaml", new YamlRuleConfigParserFactory());
        cachedFactories.put("properties", new PropertiesRuleConfigParserFactory());
    }

    public static IRuleConfigParserFactory getParserFactory(String type) {
        if(type == null || type.isEmpty()) {
            return null;
        }
        return cachedFactories.get(type);
    }
}


/**
 * 抽象工厂
 * 在简单工厂和工厂方法中类只有一种分类方式
 * 如果类有两种分类方式, 比如我们既可以按照配置文件格式来分类, 也可以按照解析的对象(Rule、System)来分类, 那么就会对应下面这8个parser类：
 *      针对规则配置的解析器：基于接口IRuleConfigParser
 *          JsonRuleConfigParser
 *          XmlRuleConfigParser
 *          YamlRuleConfigParser
 *          PropertiesRuleConfigParser
 *
 *      针对系统配置的解析器：基于接口ISystemConfigParser
 *          JsonSystemConfigParser
 *          XmlSystemConfigParser
 *          YamlSystemConfigParser
 *          PropertiesSystemConfigParser
 *
 * 针对这种场景, 如果还是用工厂方法来实现, 就要针对每个parser都编写一个工厂类,
 * 抽象工厂可以让一个工厂负责创建多个不同类型的对象(比如JsonRuleConfigParser、JsonSystemConfigParser), 有效减少工厂类的数量
 */

interface ISystemConfigParser {}
class JsonSystemConfigParser implements ISystemConfigParser {}

interface IConfigParserFactory {
    IRuleConfigParser createRuleParser();
    ISystemConfigParser createSystemParser();
}

class JsonConfigParserFactory implements IConfigParserFactory {

    @Override
    public IRuleConfigParser createRuleParser() {
        return new JsonRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new JsonSystemConfigParser();
    }
}
// ......