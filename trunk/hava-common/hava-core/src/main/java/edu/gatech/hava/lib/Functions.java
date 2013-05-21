package edu.gatech.hava.lib;

import java.util.Random;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HFunction;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.exception.IncompleteHavaException;

public final class Functions {

    private Functions() { }

    private static Random randomInstance = new Random();

    public static void resetRandom() {

        randomInstance = new Random();

    }

    public static void resetRandom(final int seed) {

        randomInstance = new Random(seed);

    }

    public static HFunction[] getAll() {

        return new HFunction[] {ROUND, FLOOR, CEILING, SQRT, LN, EXP, RANDOM};

    }

    static final HFunction ROUND = new HFunction() {

        public String getIdentifier() {

            return "round";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            HValue a = args.getElement(0);

            if (a.isInteger()) {
                return a;
            }

            if (a.isDouble()) {
                return new HValue((int) Math.round(a.doubleValue()));
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }

    };

    static final HFunction FLOOR = new HFunction() {

        public String getIdentifier() {

            return "floor";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            HValue a = args.getElement(0);

            if (a.isInteger()) {
                return a;
            }

            if (a.isDouble()) {
                return new HValue((int) Math.floor(a.doubleValue()));
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }

    };

    static final HFunction CEILING = new HFunction() {

        public String getIdentifier() {

            return "ceiling";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            HValue a = args.getElement(0);

            if (a.isInteger()) {
                return a;
            }

            if (a.isDouble()) {
                return new HValue((int) Math.ceil(a.doubleValue()));
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }

    };

    static final HFunction SQRT = new HFunction() {

        public String getIdentifier() {

            return "sqrt";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            HValue a = args.getElement(0);

            if (a.isNumerical()) {
                return new HValue(Math.sqrt(a.doubleValue()));
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }

    };

    static final HFunction LN = new HFunction() {

        public String getIdentifier() {

            return "ln";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            HValue a = args.getElement(0);

            if (a.isNumerical()) {
                return new HValue(Math.log(a.doubleValue()));
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }
    };

    static final HFunction EXP = new HFunction() {

        public String getIdentifier() {

            return "exp";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            HValue a = args.getElement(0);

            if (a.isNumerical()) {
                return new HValue(Math.exp(a.doubleValue()));
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }

    };

    static final HFunction RANDOM = new HFunction() {

        public String getIdentifier() {

            return "random";

        }

        public HValue evaluate(final HValue args)
                throws IncompleteHavaException {

            if (args.getNumElements() == 0) {
                return new HValue(randomInstance.nextDouble());
            }

            if (args.getNumElements() > 1) {
                throw new IncompleteHavaException(
                        "Too many arguments in call to function \"random\".");
            }

            HValue arg = args.getElement(0);

            if (arg.isInteger()) {
                int n = arg.intValue();
                if (n <= 0) {
                    return HValue.IGNORE;
                }
                return new HValue((int) Math.ceil(n * randomInstance.nextDouble()));
            }

            if (arg.isList()) {
                int n = arg.getNumElements();
                double r = randomInstance.nextDouble();

                for (int i = 0; i < n; i++) {
                    HValue d = arg.getElement(i);
                    double p = 1.0 / n;

                    if (d.isStructure()) {
                        HValue pv = d.structField("prob");
                        if (pv != null && pv.isDouble()) {
                            p = pv.doubleValue();
                        }
                    }

                    if (r < p) {
                        return d;
                    }

                    r -= p;
                }

                return HValue.IGNORE;
            }

            throw new IncompleteHavaException(HException.TYPE_MESSAGE);

        }

    };

}
