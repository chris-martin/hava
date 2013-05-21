package edu.gatech.hava.lib.iterator;


import edu.gatech.hava.engine.HIterator;

public final class Iterators {

    private Iterators() { }

    public static HIterator[] getAll() {

        return new HIterator[] {MAX, MIN, ARGMAX, ARGMIN, SUM, COLLECT, JOIN, FIRST, LAST, FOR};

    }

    static final HIterator MAX = new HIterator() {

        public String getIdentifier() {
            return "max";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new MaxInstance();
        }

    };

    static final HIterator MIN = new HIterator() {

        public String getIdentifier() {
            return "min";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new MinInstance();
        }

    };

    static final HIterator ARGMAX = new HIterator() {

        public String getIdentifier() {
            return "argmax";
        }

        public boolean requiresIndex() {
            return true;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new ArgmaxInstance();
        }

    };

    static final HIterator ARGMIN = new HIterator() {

        public String getIdentifier() {
            return "argmin";
        }

        public boolean requiresIndex() {
            return true;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new ArgminInstance();
        }

    };

    static final HIterator SUM = new HIterator() {

        public String getIdentifier() {
            return "sum";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new SumInstance();
        }

    };

    static final HIterator COLLECT = new HIterator() {

        public String getIdentifier() {
            return "collect";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new CollectInstance();
        }

    };

    static final HIterator JOIN = new HIterator() {

        public String getIdentifier() {
            return "join";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new JoinInstance();
        }

    };

    static final HIterator FIRST = new HIterator() {

        public String getIdentifier() {
            return "first";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new FirstInstance();
        }

    };

    static final HIterator LAST = new HIterator() {

        public String getIdentifier() {
            return "last";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new LastInstance();
        }

    };

    static final HIterator FOR = new HIterator() {

        public String getIdentifier() {
            return "for";
        }

        public boolean requiresIndex() {
            return false;
        }

        public HIterator.HIteratorInstance createInstance() {
            return new ForInstance();
        }

    };

}
