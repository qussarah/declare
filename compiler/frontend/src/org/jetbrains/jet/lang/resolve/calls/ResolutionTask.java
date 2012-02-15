/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.lang.resolve.calls;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.descriptors.CallableDescriptor;
import org.jetbrains.jet.lang.psi.Call;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.calls.autocasts.DataFlowInfo;

import java.util.Collection;

/**
 * Stores candidates for call resolution.
 *
 * @author abreslav
 */
public class ResolutionTask<D extends CallableDescriptor> {
    private final Call call;
    private final Collection<ResolvedCallImpl<D>> candidates;
    private final DataFlowInfo dataFlowInfo;
    private DescriptorCheckStrategy checkingStrategy;


    public ResolutionTask(
            @NotNull Collection<ResolvedCallImpl<D>> candidates,
            @NotNull Call call,
            @NotNull DataFlowInfo dataFlowInfo
    ) {
        this.candidates = candidates;
        this.call = call;
        this.dataFlowInfo = dataFlowInfo;
    }

    @NotNull
    public DataFlowInfo getDataFlowInfo() {
        return dataFlowInfo;
    }

    @NotNull
    public Collection<ResolvedCallImpl<D>> getCandidates() {
        return candidates;
    }

    @NotNull
    public Call getCall() {
        return call;
    }

    public void setCheckingStrategy(DescriptorCheckStrategy strategy) {
        checkingStrategy = strategy;
    }

    public boolean performAdvancedChecks(D descriptor, BindingTrace trace, TracingStrategy tracing) {
        if (checkingStrategy != null && !checkingStrategy.performAdvancedChecks(descriptor, trace, tracing)) {
            return false;
        }
        return true;
    }

    public interface DescriptorCheckStrategy {
        <D extends CallableDescriptor> boolean performAdvancedChecks(D descriptor, BindingTrace trace, TracingStrategy tracing);
    }
}
