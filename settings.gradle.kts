rootProject.name = "financial-manager"

include(":expenses-manager")
include(":expenses-manager:domain")
include(":common")
include("expenses-manager:use-case")
include("expenses-manager:persistence")
findProject(":expenses-manager:persistence")?.name = "persistence"
include("expenses-manager:rest")
findProject(":expenses-manager:rest")?.name = "rest"
include("expenses-manager:application")
findProject(":expenses-manager:application")?.name = "application"
