package me.rasztabiga.fridgy.products.web.rest.vm

import me.rasztabiga.fridgy.products.service.dto.AdminUserDTO

/**
 * View Model extending the [AdminUserDTO], which is meant to be used in the user management UI.
 */
class ManagedUserVM : AdminUserDTO() {

    override fun toString() = "ManagedUserVM{${super.toString()}}"
}
