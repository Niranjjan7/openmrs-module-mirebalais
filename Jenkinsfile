node('MASTER') {
    // some block
    stage('SCM') {
    // some block
    git 'https://github.com/Niranjjan7/openmrs-module-mirebalais.git'
    stage('build') {
    // some block
    sh 'mvn clean package'

}
stage('postbuild') {
        junit '**/TEST-*.xml'
        archive '**/*.war'
}

}