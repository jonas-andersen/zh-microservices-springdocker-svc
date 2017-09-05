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
			post {
				success {
					withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'gogs-gituser', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
						sh("git tag -a build-${env.BUILD_NUMBER} -m 'Jenkins build success'")
						// Beware that this has the full credentials in the url, which is part of the command line (shows up in "ps")
						sh("git push http://${env.GIT_USERNAME}:${env.GIT_PASSWORD}@${DOCKER_GATEWAY_IP}:3000/gituser/todo-svc.git --tags")
					}
				}
			}
		}
		
		stage('Staging') {
			steps {
				echo 'Publishing to staging environment'
				lock('container-todo-svc-staging') {
					sh 'docker stop todo-svc-staging || true'  // Stop current container, ignore if fails
					sh 'docker rm todo-svc-staging || true'  // remove current container, ignore if fails
					sh "docker run -d --name todo-svc-staging -p 8180:8080 todo-svc:${env.BUILD_NUMBER}"
				}
			}
		
		}
		
		stage('Approve PROD deployment') {
			steps {
				milestone(50)
				timeout(time:5, unit:'HOURS') {
					input 'Promote to production?'
				}
				milestone(55)
			}
		}

		stage('Production') {
			steps {
				echo 'Publishing to production environment'				

				lock('container-todo-svc-prod') {
					sh 'docker stop todo-svc-prod || true'  // Stop current container, ignore if fails
					sh 'docker rm todo-svc-prod || true'  // remove current container, ignore if fails
					sh "docker run -d --name todo-svc-prod -p 8190:8080 todo-svc:${env.BUILD_NUMBER}"
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
