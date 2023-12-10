rootProject.name = "financial-manager"

include(":expenses-manager")
include(":expenses-manager:domain")
include(":common")
include("expenses-manager:use-case")
include("expenses-manager:persistence")
include("expenses-manager:rest")
include("expenses-manager:application")