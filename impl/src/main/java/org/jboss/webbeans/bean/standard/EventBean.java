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
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.event.Event;
import javax.event.Fires;
import javax.inject.Obtains;
import javax.inject.TypeLiteral;

import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.event.EventImpl;
import org.jboss.webbeans.injection.resolution.AnnotatedItemTransformer;
import org.jboss.webbeans.literal.FiresLiteral;

public class EventBean extends AbstractFacadeBean<Event<?>>
{

   private static final Class<Event<?>> TYPE = new TypeLiteral<Event<?>>() {}.getRawType();
   private static final Set<Type> DEFAULT_TYPES = new HashSet<Type>(Arrays.asList(TYPE, Object.class));
   private static final Fires FIRES = new FiresLiteral();
   private static final Set<Annotation> DEFAULT_BINDINGS = new HashSet<Annotation>(Arrays.asList(FIRES));
   public static final AnnotatedItemTransformer TRANSFORMER = new FacadeBeanAnnotatedItemTransformer(Event.class, FIRES);
   private static final Set<Class<? extends Annotation>> FILTERED_ANNOTATION_TYPES = new HashSet<Class<? extends Annotation>>(Arrays.asList(Obtains.class));
   
   
   public static AbstractFacadeBean<Event<?>> of(ManagerImpl manager)
   {
      return new EventBean(manager);
   }
   
   protected EventBean(ManagerImpl manager)
   {
      super(manager);
   }

   @Override
   public Class<Event<?>> getType()
   {
      return TYPE;
   }

   @Override
   public Set<Type> getTypes()
   {
      return DEFAULT_TYPES;
   }
   
   @Override
   public Set<Annotation> getBindings()
   {
      return DEFAULT_BINDINGS;
   }

   @Override
   protected Event<?> newInstance(Type type, Set<Annotation> annotations)
   {
      return EventImpl.of(type, getManager(), annotations);
   }

   @Override
   protected Set<Class<? extends Annotation>> getFilteredAnnotationTypes()
   {
      return FILTERED_ANNOTATION_TYPES;
   }
   
   @Override
   public String toString()
   {
      return "Built-in implicit javax.event.Event bean";
   }
   
}
