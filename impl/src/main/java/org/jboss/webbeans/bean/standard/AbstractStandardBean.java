/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.webbeans.bean.standard;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.context.Dependent;
import javax.inject.Standard;

import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.bean.RIBean;
import org.jboss.webbeans.bootstrap.BeanDeployerEnvironment;
import org.jboss.webbeans.injection.AnnotatedInjectionPoint;
import org.jboss.webbeans.literal.CurrentLiteral;

public abstract class AbstractStandardBean<T> extends RIBean<T>
{
   
   private static final Annotation[] DEFAULT_BINDING_ARRAY = { new CurrentLiteral() };
   private static final Set<Annotation> DEFAULT_BINDING = new HashSet<Annotation>(Arrays.asList(DEFAULT_BINDING_ARRAY));
   
   private final String id;
   
   protected AbstractStandardBean(ManagerImpl manager)
   {
      super(manager);
      this.id = getClass().getSimpleName();
   }
   
   @Override
   public void initialize(BeanDeployerEnvironment environment)
   {
      // No-op
   }

   
   
   @Override
   public Set<Annotation> getBindings()
   {
      return DEFAULT_BINDING;
   }
   
   @Override
   public Class<? extends Annotation> getDeploymentType()
   {
      return Standard.class;
   }
   
   @Override
   public Class<? extends Annotation> getScopeType()
   {
      return Dependent.class;
   }
   
   @Override
   public RIBean<?> getSpecializedBean()
   {
      return null;
   }
   
   @Override
   public String getName()
   {
      return null;
   }
   
   @Override
   public Set<AnnotatedInjectionPoint<?, ?>> getAnnotatedInjectionPoints()
   {
      return Collections.emptySet();
   }
   
   @Override
   public boolean isNullable()
   {
      return true;
   }
   
   @Override
   public boolean isPrimitive()
   {
      return false;
   }
   
   @Override
   public boolean isSerializable()
   {
      return false;
   }
   
   @Override
   public boolean isSpecializing()
   {
      return false;
   }
   
   @Override
   public boolean isProxyable()
   {
      return false;
   }
   
   @Override
   public String getId()
   {
      return id;
   }
   
}
