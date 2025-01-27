package com.github.gradle.node.npm.task

import com.github.gradle.AbstractIntegTest
import com.github.gradle.node.NodeExtension
import org.gradle.testkit.runner.TaskOutcome

import java.util.regex.Pattern

class NpmRule_integTest extends AbstractIntegTest {
    def 'execute npm_install rule'() {
        given:
        writeBuild('''
            plugins {
                id 'com.github.node-gradle.node'
            }
        ''')
        writeEmptyPackageJson()

        when:
        def result = buildTask('npm_install')

        then:
        result.outcome == TaskOutcome.SUCCESS
    }

   def 'can configure npm_ rule task'() {
        given:
        writeBuild('''
            plugins {
                id 'com.github.node-gradle.node'
            }

            npm_run_build {
                doFirst { project.logger.info('configured') }
            }
        ''')
        writeEmptyPackageJson()

        when:
        def result = buildTask('help')

        then:
        result.outcome == TaskOutcome.SUCCESS
   }

    def 'can execute an npm module using npm_run_'() {
        given:
        writeBuild('''
            plugins {
                id 'com.github.node-gradle.node'
            }
        ''')

        copyResources("fixtures/npm-missing/package.json", "package.json")

        when:
        def result = buildTask('npm_run_echoTest')

        then:
        result.outcome == TaskOutcome.SUCCESS
        fileExists('test.txt')
    }

    def 'succeeds to run npm module using npm_run_ when the package.json file contains local npm'() {
        given:
        writeBuild('''
            plugins {
                id 'com.github.node-gradle.node'
            }
        ''')

        copyResources("fixtures/npm-present/")

        when:
        def result = build('npm_run_npmVersion')

        then:
        result.task(":npmInstall").outcome == TaskOutcome.SUCCESS
        result.task(":npm_run_npmVersion").outcome == TaskOutcome.SUCCESS
        def versionPattern = Pattern.compile(".*Version\\s+${NodeExtension.DEFAULT_NPM_VERSION}.*", Pattern.DOTALL)
        versionPattern.matcher(result.output).find()
    }
}
