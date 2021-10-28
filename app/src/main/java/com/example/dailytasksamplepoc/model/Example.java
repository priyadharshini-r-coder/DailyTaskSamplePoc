package com.example.dailytasksamplepoc.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Example {


        private Integer count;

        private List<Entry> entries = null;

        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Example() {
        }

        /**
         *
         * @param entries
         * @param count
         */
        public Example(Integer count, List<Entry> entries) {
            super();
            this.count = count;
            this.entries = entries;
        }

        public Integer getCount() {
            return count;
        }


        public void setCount(Integer count) {
            this.count = count;
        }


        public List<Entry> getEntries() {
            return entries;
        }


        public void setEntries(List<Entry> entries) {
            this.entries = entries;
        }


        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }
}
