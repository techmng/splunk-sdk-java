/*
 * Copyright 2014 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.splunk;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Specifies a filter in a pivot on a numeric field.
 */
public class NumberPivotFilter extends PivotFilter {
    private final NumberComparison comparison;
    private final double comparisonValue;

    NumberPivotFilter(DataModelObject dataModelObject, String field,
                             NumberComparison comparison, double comparisonValue) {
        super(dataModelObject, field);
        if (dataModelObject.getField(field).getType() != FieldType.NUMBER) {
            throw new IllegalArgumentException("Field " + field + " on the data model object was of type "
                    + dataModelObject.getField(field).getType().toString() + ", expected number.");
        }
        this.comparison = comparison;
        this.comparisonValue = comparisonValue;
    }

    @Override
    JsonElement toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);

        JsonObject filterRule = new JsonObject();
        filterRule.add("comparator", new JsonPrimitive(this.comparison.toString()));
        filterRule.add("compareTo", new JsonPrimitive(this.comparisonValue));

        root.add("rule", filterRule);

        return root;
    }
}
