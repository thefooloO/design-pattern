package creation;

/**
 * 建造者模式
 */

/**
 * example: 资源池配置类
 */
class ResourcePoolConfig1 {

    private static final int DEFAULT_MAX_TOTAL = 8;
    private static final int DEFAULT_MAX_IDLE  = 8;
    private static final int DEFAULT_MIN_IDLE  = 0;

    private String name;
    private int maxTotal = DEFAULT_MAX_TOTAL;
    private int maxIdle  = DEFAULT_MAX_IDLE;
    private int minIdle  = DEFAULT_MIN_IDLE;

    public ResourcePoolConfig1(String name, Integer maxTotal, Integer maxIdle, Integer minIdle) {
        this.name = name;

        if(maxTotal != null)
            this.maxTotal = maxTotal;

        if(maxIdle  != null)
            this.maxIdle = maxIdle;

        if(minIdle  != null)
            this.minIdle = minIdle;
    }

    public static void main(String[] args) {
        // 参数太多、可读性差、参数可能传递错误
        ResourcePoolConfig1 config = new ResourcePoolConfig1("db",16,null,1);
    }
}


/**
 * 优化方案：通过set()函数来设置
 */
class ResourcePoolConfig2 {

    private static final int DEFAULT_MAX_TOTAL = 8;
    private static final int DEFAULT_MAX_IDLE  = 8;
    private static final int DEFAULT_MIN_IDLE  = 0;

    private String name;
    private int maxTotal = DEFAULT_MAX_TOTAL;
    private int maxIdle  = DEFAULT_MAX_IDLE;
    private int minIdle  = DEFAULT_MIN_IDLE;

    public ResourcePoolConfig2(String name) {
        this.name = name;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public static void main(String[] args) {
        ResourcePoolConfig2 config = new ResourcePoolConfig2("db");
        config.setMaxTotal(16);
        config.setMinIdle(1);
    }

}

/**
 * 如果必填项很多、配置项之间有依赖关系、对象在创建好之后不能修改内部属性值(不能暴露set方法), 就需要使用建造者模式
 */
class ResourcePoolConfig3 {

    private String name;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;

    private ResourcePoolConfig3(Builder builder) {
        this.name = builder.name;
        this.maxTotal = builder.maxTotal;
        this.maxIdle = builder.maxIdle;
        this.minIdle = builder.minIdle;
    }


    public static class Builder {

        private static final int DEFAULT_MAX_TOTAL = 8;
        private static final int DEFAULT_MAX_IDLE  = 8;
        private static final int DEFAULT_MIN_IDLE  = 0;

        private String name;
        private int maxTotal = DEFAULT_MAX_TOTAL;
        private int maxIdle  = DEFAULT_MAX_IDLE;
        private int minIdle  = DEFAULT_MIN_IDLE;

        public ResourcePoolConfig3 build() {
            return new ResourcePoolConfig3(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        public Builder setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
            return this;
        }

        public Builder setMinIdle(int minIdle) {
            this.minIdle = minIdle;
            return this;
        }

    }

    public static void main(String[] args) {
        ResourcePoolConfig3 config = new ResourcePoolConfig3.Builder().setName("db").setMaxTotal(16).build();
    }
}