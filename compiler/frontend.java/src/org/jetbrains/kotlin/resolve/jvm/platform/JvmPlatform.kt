/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package org.jetbrains.kotlin.resolve.jvm.platform

import org.jetbrains.kotlin.platform.JvmBuiltIns
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import java.util.*

object JvmPlatform : TargetPlatform("JVM") {
    private val defaultImports = LockBasedStorageManager().createMemoizedFunction<Boolean, List<ImportPath>> { includeKotlinComparisons ->
        ArrayList<ImportPath>().apply {
            addAll(Default.getDefaultImports(includeKotlinComparisons))

            add(ImportPath.fromString("java.lang.*"))
            add(ImportPath.fromString("kotlin.jvm.*"))

            fun addAllClassifiersFromScope(scope: MemberScope) {
                for (descriptor in scope.getContributedDescriptors(DescriptorKindFilter.CLASSIFIERS, MemberScope.ALL_NAME_FILTER)) {
                    add(ImportPath(DescriptorUtils.getFqNameSafe(descriptor), false))
                }
            }

            for (builtinPackageFragment in JvmBuiltIns(LockBasedStorageManager.NO_LOCKS).builtInsPackageFragmentsImportedByDefault) {
                addAllClassifiersFromScope(builtinPackageFragment.getMemberScope())
            }
        }

    }

    override fun getDefaultImports(includeKotlinComparisons: Boolean): List<ImportPath> = defaultImports(includeKotlinComparisons)

    override val platformConfigurator: PlatformConfigurator = JvmPlatformConfigurator

    override val multiTargetPlatform = MultiTargetPlatform.Specific(platformName)
}
