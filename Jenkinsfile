/*
    Jenkins Pipeline File
    Purpose: Defines a Windows-friendly CI pipeline for checking out the framework,
    running the regression suite, and publishing test artifacts.
    Why this way: Pipeline-as-code keeps CI configuration versioned with the framework
    so every team member and Jenkins environment runs the same steps.
*/
pipeline {
    agent any

    /*
        Tools section tells Jenkins which globally configured JDK and Maven installations
        should be placed on PATH for this build. Update these names if your Jenkins
        global tool configuration uses different labels.
    */
    tools {
        jdk 'JDK11'
        maven 'Maven3'
    }

    environment {
        // Centralized environment variables make the build command easy to adjust later.
        MAVEN_ARGS = 'clean test -Dsurefire.suiteXmlFiles=testng-regression.xml -Dbrowser=chrome'
    }

    options {
        // Timestamps make console logs easier to correlate with screenshots and report files.
        timestamps()

        // Prevent overlapping runs of the same UI suite on one Jenkins job.
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                /*
                    Checkout directly from GitHub main branch.
                    If the repo becomes private later, add Jenkins credentials here.
                */
                git branch: 'main', url: 'https://github.com/dbohara88/OrangeHRMAutomation.git'
            }
        }

        stage('Build And Test') {
            steps {
                /*
                    `bat` is used because this pipeline targets a Windows Jenkins agent.
                    The command runs the regression suite in Chrome, which is a good
                    balance between speed and confidence for CI.
                */
                bat "mvn %MAVEN_ARGS%"
            }
        }
    }

    post {
        always {
            /*
                JUnit publisher helps Jenkins display pass/fail trends and per-test results.
                Surefire writes XML files that Jenkins can read after every build.
            */
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            /*
                Archive HTML reports and screenshots so they remain downloadable
                from Jenkins even after the workspace is cleaned.
            */
            archiveArtifacts artifacts: 'reports/*.html', allowEmptyArchive: true
            archiveArtifacts artifacts: 'screenshots/**/*.png', allowEmptyArchive: true
        }
    }
}
