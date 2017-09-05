#!groovy
pipeline {
	agent any

	tools {
		maven 'Maven 3.3.9'
	}

	environment {
		IMAGE = readMavenPom().getArtifactId()
		POM_VERSION = readMavenPom().getVersion()
		BUILD_ID = "${currentBuild.number}"
		BUILD_RELEASE_VERSION = readMavenPom().getVersion().replace("-SNAPSHOT", "")
	}

	stages {
		/* Uncomment if using explicit checkout
		stage('Checkout') {
			steps {
				checkout scm
			}
		}*/

		stage('Build') {
			steps {
				echo "Performing build of ${IMAGE} version ${BUILD_RELEASE_VERSION}.${currentBuild.number} (${POM_VERSION})"
				echo "Build id: ${BUILD_ID}"
				echo "M2_HOME = ${M2_HOME}"

				// Set the version to the Maven version replacing -SNAPSHOT with the build number
				sh "mvn versions:set -DnewVersion=${BUILD_RELEASE_VERSION}.${currentBuild.number}"

				// Build the application
				sh 'mvn install'

				// Set the version back to -SNAPSHOT
				sh "mvn versions:set -DnewVersion=${POM_VERSION}"
			}
			post {
				success {
					archiveArtifacts "target/todo-svc.jar"
				}
				always {
					junit 'target/surefire-reports/**/*.xml' 
				}
			}
		}

		stage('Build Image') {
			steps {
				echo 'Creating docker image'

				script {
					def newApp = docker.build "todo-svc:${env.BUILD_NUMBER}"
				}
			} 
		}
	}

	post {
		// Always runs. And it runs before any of the other post conditions.
		always {
			// Clear the workspace
			deleteDir()
		}
	}

	// The options directive is for configuration that applies to the whole job.
	options {
		// Keep 10 last builds only
		buildDiscarder(logRotator(numToKeepStr:'10'))
	}

}
