package edu.gatech.hava.hdt.builder;

import org.eclipse.core.resources.IFile;

class HavaErrorHandler {

    private IFile file;

    public HavaErrorHandler(final IFile file) {

        this.file = file;

    }

//        private void addMarker(final HException e, final int severity) {
//            HavaBuilder.this.addMarker(file, e.getMessage(), e
//                    .getLineNumber(), severity);
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public void error(final HException exception) {
//            addMarker(exception, IMarker.SEVERITY_ERROR);
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public void fatalError(final HException exception) {
//            addMarker(exception, IMarker.SEVERITY_ERROR);
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public void warning(final SAXParseException exception) {
//            addMarker(exception, IMarker.SEVERITY_WARNING);
//        }

}
