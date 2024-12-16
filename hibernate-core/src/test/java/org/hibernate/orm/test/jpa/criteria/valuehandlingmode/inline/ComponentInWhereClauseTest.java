/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.jpa.criteria.valuehandlingmode.inline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.cfg.AvailableSettings;

import org.hibernate.testing.orm.junit.EntityManagerFactoryScope;
import org.hibernate.testing.orm.junit.Jpa;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Jpa(
		annotatedClasses = {
				ComponentInWhereClauseTest.Employee.class,
				ComponentInWhereClauseTest.Project.class,
				ComponentInWhereClauseTest.Person.class
		}
		, properties = @Setting(name = AvailableSettings.CRITERIA_VALUE_HANDLING_MODE, value = "inline")
)
public class ComponentInWhereClauseTest {
	private Projects projects;

	@BeforeAll
	public void setUp(EntityManagerFactoryScope scope) {
		scope.inTransaction(
				entityManager -> {
					projects = new Projects();
					projects.addPreviousProject( new Project( "First" ) );
					projects.addPreviousProject( new Project( "Second" ) );
					projects.setCurrentProject( new Project( "Third" ) );

					ContactDetail contactDetail = new ContactDetail();
					contactDetail.setEmail( "abc@mail.org" );
					contactDetail.addPhone( new Phone( "+4411111111" ) );

					final Employee employee = new Employee();
					employee.setProjects( projects );
					employee.setContactDetail( contactDetail );
					entityManager.persist( employee );

					final Person person = new Person();
					person.setInformation( new Information() );
					ContactDetail infoContactDetail = new ContactDetail();
					infoContactDetail.setEmail( "xyz@mail.org" );
					infoContactDetail.addPhone( new Phone( "999-999-9999" ) );
					person.getInformation().setInfoContactDetail( infoContactDetail );
					entityManager.persist( person );
				}
		);
	}

	@Test
	public void testInExpressionForTheManyToOnePropertyOfAComponent(EntityManagerFactoryScope scope) {
		scope.inTransaction(
				entityManager -> {
					CriteriaBuilder builder = entityManager.getCriteriaBuilder();
					CriteriaQuery<Employee> query = builder.createQuery( Employee.class );
					Root<Employee> root = query.from( Employee.class );

					query.where( root.get( "projects" )
										.get( "currentProject" )
										.in( projects.getCurrentProject() ) );

					final List<Employee> results = entityManager.createQuery( query ).getResultList();
					assertThat( results.size(), is( 1 ) );
				}
		);
	}

	@MappedSuperclass
	public static abstract class AbstractEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		protected Long id;

		public Long getId() {
			return id;
		}
	}

	@Entity(name = "Employee")
	@Table(name = "EMPLOYEE")
	public static class Employee extends AbstractEntity {

		@Embedded
		private Projects projects;

		@Embedded
		private ContactDetail contactDetail;

		public void setProjects(Projects projects) {
			this.projects = projects;
		}

		public void setContactDetail(ContactDetail contactDetail) {
			this.contactDetail = contactDetail;
		}
	}

	@Embeddable
	public static class ContactDetail {
		private String email;

		@ElementCollection
		private List<Phone> phones = new ArrayList<>();

		public void addPhone(Phone phone) {
			this.phones.add( phone );
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	@Embeddable
	public static class Projects {

		@OneToMany(cascade = CascadeType.PERSIST)
		private Set<Project> previousProjects = new HashSet<>();

		@ManyToOne(cascade = CascadeType.PERSIST)
		private Project currentProject;

		public void addPreviousProject(Project project) {
			this.previousProjects.add( project );
		}

		public Set<Project> getPreviousProjects() {
			return previousProjects;
		}

		public Project getCurrentProject() {
			return currentProject;
		}

		public void setCurrentProject(Project project) {
			this.currentProject = project;
		}
	}

	@Entity(name = "Project")
	@Table(name = "PROJECT")
	public static class Project extends AbstractEntity {

		private String name;

		public Project() {
		}

		public Project(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Embeddable
	public static class Phone {
		@Column(name = "phone_number")
		private String number;

		public Phone() {
		}

		public Phone(String number) {
			this.number = number;
		}

		public String getNumber() {
			return this.number;
		}
	}

	@Entity(name = "Person")
	@Table(name = "PERSON")
	public static class Person extends AbstractEntity {
		@Embedded
		private Information information;

		public Information getInformation() {
			return information;
		}

		public void setInformation(Information information) {
			this.information = information;
		}
	}

	@Embeddable
	public static class Information {
		@Embedded
		private ContactDetail infoContactDetail;

		public ContactDetail getInfoContactDetail() {
			return infoContactDetail;
		}

		public void setInfoContactDetail(ContactDetail infoContactDetail) {
			this.infoContactDetail = infoContactDetail;
		}
	}
}
